package pms.server

import pms.algebra.http._
import pms.algebra.imdb._
import pms.algebra.movie.MovieAlgebra
import pms.algebra.user._
import pms.config._
import pms.db.config._
import pms.db._
import pms.email._
import pms.logger._
import pms._
import pms.rest.movie.MovieAPI
import pms.rest.user.UserAPI
import pms.server.config._
import pms.service.movie.IMDBService
import pms.service.user.UserAccountService

import org.http4s.server._
import org.http4s._

/** @author Lorand Szakacs, https://github.com/lorandszakacs
  * @since 11 Jul 2018
  */
final class PMSWeave[F[_]] private (
  serverConfig: PMSServerConfig,
  middleware:   AuthMiddleware[F, AuthCtx],
  userAPI:      UserAPI[F],
  movieAPI:     MovieAPI[F],
)(implicit F:   Async[F]) {

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

//    val routes = NonEmptyList.of[HttpRoutes[F]](userAPI.routes).reduceK
//    val authed = NonEmptyList.of[AuthCtxRoutes[F]](userAPI.authedRoutes, movieAPI.authedRoutes).reduceK
    val pmsAPI: HttpRoutes[F] = HttpRoutes.empty[F]

    Router[F](("api", pmsAPI)).orNotFound
  }

}

object PMSWeave {

  @scala.annotation.nowarn
  def resource[F[_]](implicit
    timer:            Temporal[F],
    mainContextShift: Async[F],
  ): Resource[F, PMSWeave[F]] =
    for {
      implicit0(config: Config[F]) <- Config.resource[F]
      implicit0(logging: Logging[F]) <- Logging.resource[F]
      implicit0(random: Random[F]) <- Random.resource[F]
      implicit0(secureRandom: SecureRandom[F]) <- SecureRandom.resource[F]
      implicit0(logger: Logger[F]) = logging.of(this)

      config <- PMSServerConfig.resource[F]

      _         <- FlywayAlgebra
        .resource[F](config.dbConfig.connection)
        .evalMap(flyway => if (config.dbConfig.forceClean) flyway.cleanDB(logger).map(_ => flyway) else flyway.pure[F])
        .evalMap(flyway => flyway.runMigrations(logger))

      implicit0(transactor: Transactor[F]) <- TransactorAlgebra.resource[F](config.dbConfig.connection)

      throttler <- EffectThrottler.resource[F](
        config.imdbConfig.requestsInterval,
        config.imdbConfig.requestsNumber,
      )

      imdbAlgebra    <- IMDBAlgebra.resource[F](throttler)
      authAlgebra    <- UserAuthAlgebra.resource[F]
      accountAlgebra <- UserAccountAlgebra.resource[F]
      userAlgebra    <- UserAlgebra.resource[F]
      movieAlgebra   <- MovieAlgebra.resource[F](authAlgebra)
      emailAlgebra   <- EmailAlgebra.resource[F](config.emailConfig)

      imdbService <- IMDBService.resource[F](movieAlgebra, imdbAlgebra)
      userService <- UserAccountService.resource[F](accountAlgebra, emailAlgebra)

      middleware <- AuthedHttp4s.userTokenAuthMiddleware[F](authAlgebra)

      movieAPI <- MovieAPI.resource(imdbService, movieAlgebra)
      userAPI  <- UserAPI.resource(userAlgebra, authAlgebra, userService)

    } yield new PMSWeave[F](config, middleware, userAPI, movieAPI)

}
