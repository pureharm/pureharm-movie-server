package phms.server

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
import phms.rest.movie.MovieAPI
import phms.rest.user.UserAPI
import phms.server.config._
import phms.service.movie.IMDBService
import phms.service.user.UserAccountService
import org.http4s.server._
import org.http4s._

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
)(implicit F:           Async[F], console: Console[F]) {

  def serverResource: Resource[F, Server] = {
    import org.http4s.ember.server.EmberServerBuilder
    EmberServerBuilder
      .default[F]
      .withPort(serverConfig.httpConfig.port)
      .withHost(serverConfig.httpConfig.host)
      .withHttpApp(http4sApp)
      .withoutTLS
      .build
  }

  private def http4sApp: HttpApp[F] = {
    import org.http4s.implicits.http4sKleisliResponseSyntaxOptionT

    val routes = NonEmptyList.of[HttpRoutes[F]](userAPI.routes).reduceK
    val authed = NonEmptyList.of[AuthCtxRoutes[F]](userAPI.authedRoutes, movieAPI.authedRoutes).reduceK
    val phmsAPI: HttpRoutes[F] = routes <+> middleware(authed)

    Router[F](("api", phmsAPI)).orNotFound
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

      imdbService <- IMDBService.resource[F](movieAlgebra, imdbAlgebra)
      userService <- UserAccountService.resource[F](accountAlgebra, emailPort)

      middleware <- AuthedHttp4s.userTokenAuthMiddleware[F](authAlgebra)

      movieAPI <- MovieAPI.resource(imdbService, movieAlgebra)
      userAPI  <- UserAPI.resource(userAlgebra, authAlgebra, userService)

    } yield new PHMSWeave[F](
      config,
      middleware,
      userAPI,
      movieAPI,
      userBootstrapAlgebra = userBootstrapAlgebra,
      userAccountAlgebra   = accountAlgebra,
    )

}
