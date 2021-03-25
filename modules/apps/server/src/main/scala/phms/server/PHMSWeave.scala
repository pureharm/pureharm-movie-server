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

import fs2.io.net.Network
import phms.stack.http._
import phms.algebra.imdb._
import phms.algebra.movie.MovieAlgebra
import phms.algebra.user._
import phms.config._
import phms.time._
import phms.db._
import phms.port.email._
import phms.logger._
import phms._
import phms.api.movie.MovieAPI
import phms.api.user.UserAPI
import phms.server.config._
import phms.organizer.movie.IMDBOrganizer
import phms.organizer.user.UserAccountOrganizer
import org.http4s.server._
import org.http4s._
import phms.http.PHMSErrorHandler

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
  errorHandler:         PHMSErrorHandler[F],
)(implicit F:           Async[F], logging: Logging[F]) {

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

  @scala.annotation.nowarn
  def resource[F[_]](implicit
    timer:            Temporal[F],
    mainContextShift: Async[F],
  ): Resource[F, PHMSWeave[F]] =
    for {
      implicit0(console: Console[F]) <- Console.make[F].pure[Resource[F, *]]
      implicit0(config: Config[F]) <- Config.resource[F]
      implicit0(logging: Logging[F]) <- Logging.resource[F]
      implicit0(random: Random[F]) <- Random.resource[F]
      implicit0(time: Time[F]) <- Time.resource[F]
      implicit0(network: Network[F]) <- Resource.pure(Network.forAsync[F])
      implicit0(secureRandom: SecureRandom[F]) <- SecureRandom.resource[F]
      implicit0(supervisor: Supervisor[F]) <- Supervisor[F]

      implicit0(logger: Logger[F]) = logging.of(this)

      config <- PHMSServerConfig.resource[F]

      _         <- Flyway
        .resource[F](config.dbConfig.connection, config.dbConfig.flyway)
        .evalMap(flyway => flyway.runMigrations(logger))

      implicit0(dbPool: DBPool[F]) <- DBPool.resource[F](config.dbConfig.connection)

      throttler <- EffectThrottler.resource[F](
        config.imdbConfig.requestsInterval,
        config.imdbConfig.requestsNumber,
      )

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

      errorHandler <- PHMSErrorHandler.resource[F]
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
