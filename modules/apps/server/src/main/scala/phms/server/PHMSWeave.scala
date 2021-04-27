/*
 * Copyright 2021 BusyMachines
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package phms.server

import phms.http.*
import phms.config.*
import phms.time.*
import phms.db.*
import phms.port.email.*
import phms.logger.*
import phms.stack.http.{*, given}
import phms.algebra.imdb.*
import phms.algebra.movie.MovieAlgebra
import phms.algebra.user.*
import phms.organizer.movie.*
import phms.organizer.user.*
import phms.api.movie.*
import phms.api.user.*
import phms.server.config.*
import phms.*

import org.http4s.server.*
import org.http4s.*
import fs2.io.net.Network

/** @author Lorand Szakacs, https://github.com/lorandszakacs
  * @since 11 Jul 2018
  */
final class PHMSWeave[F[_]] private (
  serverConfig:         PHMSServerConfig,
  middleware:           AuthMiddleware[F, AuthCtx],
  userAPI:              UserAPI[F],
  movieAPI:             MovieAPI[F],
  //TODO: add all modules here
  userBootstrapAlgebra: UserAccountBootstrapAlgebra[F],
  userAccountAlgebra:   UserAccountAlgebra[F],
  errorHandler:         PHMSHttp4sErrorHandler[F],
)(using F:           Async[F], logging: Logging[F]) {

  def serverResource: Resource[F, Server] = {
    import org.http4s.ember.server.EmberServerBuilder
    EmberServerBuilder
      .default[F]
      .withPort(serverConfig.httpConfig.port)
      .withHost(serverConfig.httpConfig.host)
      .withErrorHandler(errorHandler)
      .withHttpApp(http4sApp)
      .withoutTLS
      .build
  }

  private def http4sApp: HttpApp[F] = {
    import org.http4s.implicits.http4sKleisliResponseSyntaxOptionT

    val routes = NonEmptyList.of[HttpRoutes[F]](userAPI.routes).reduceK
    val authed = NonEmptyList.of[AuthCtxRoutes[F]](userAPI.authedRoutes, movieAPI.authedRoutes).reduceK
    val phmsAPI: HttpRoutes[F] = routes <+> middleware(authed)

    Router[F](("phms/api", phmsAPI)).orNotFound
  }

  def bootstrapServer: F[Unit] = for {
    _ <-
      if (serverConfig.boostrap) {
        phms.server.bootstrap.Bootstrap
          .bootstrap[F](
            userAccountAlgebra,
            userBootstrapAlgebra,
          )
          .attempt
          .void
      }
      else F.unit
  } yield ()

}

object PHMSWeave {

  def resource[F[_]](using Temporal[F], Async[F]): Resource[F, PHMSWeave[F]] = {
    for {
      given Console[F]      <- Console.make[F].pure[Resource[F, *]]
      given Time[F]         <- Time.resource[F]
      given Config[F]       <- Config.resource[F]
      given Logging[F]      <- Logging.resource[F]
      given Random[F]       <- Random.resource[F]
      given SecureRandom[F] <- SecureRandom.resource[F]
      given Supervisor[F]   <- Supervisor[F]
      given Logger[F]       = Logging[F].named("phms.weave")
      config                <- PHMSServerConfig.resource[F]
      given DBPool[F]       <- DBPool.resource[F](config.dbConfig.connection)

      _ <- Flyway
        .resource[F](config.dbConfig.connection, config.dbConfig.flyway)
        .evalMap(flyway => flyway.runMigrations(using Logger[F]))

      throttler <- EffectThrottler.resource[F](config.imdbConfig.requestsInterval, config.imdbConfig.requestsNumber)

      emailPort <- EmailPort.resource[F](config.emailConfig)

      imdbAlgebra          <- IMDBAlgebra.resource[F](throttler)
      authAlgebra          <- UserAuthAlgebra.resource[F]
      accountAlgebra       <- UserAccountAlgebra.resource[F]
      userAlgebra          <- UserAlgebra.resource[F]
      userBootstrapAlgebra <- UserAccountBootstrapAlgebra.resource[F](accountAlgebra)
      movieAlgebra         <- MovieAlgebra.resource[F](authAlgebra)

      imdbOrganizer        <- IMDBOrganizer.resource[F](movieAlgebra, imdbAlgebra)
      userAccountOrganizer <- UserAccountOrganizer.resource[F](accountAlgebra, emailPort)

      middleware <- AuthedHttp4s.userTokenAuthMiddleware[F](authAlgebra)

      movieAPI <- MovieAPI.resource(imdbOrganizer, movieAlgebra)
      userAPI  <- UserAPI.resource(userAlgebra, authAlgebra, userAccountOrganizer)

      errorHandler <- PHMSHttp4sErrorHandler.resource[F]
    } yield new PHMSWeave[F](
      config,
      middleware,
      userAPI,
      movieAPI,
      userBootstrapAlgebra = userBootstrapAlgebra,
      userAccountAlgebra   = accountAlgebra,
      errorHandler         = errorHandler,
    )
  }

}
