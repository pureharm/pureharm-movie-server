package pms.server

import org.http4s.server.blaze.BlazeServerBuilder
import org.http4s.server._
import org.http4s._
import pms.algebra.http._
import pms.algebra.imdb._
import pms.algebra.movie.MovieAlgebra
import pms.algebra.user._
import pms.config._
import pms.db.config._
import pms.db._
import pms.email._
import pms.logger._
import pms.core._
import pms.rest.movie.MovieAPI
import pms.rest.user.UserAPI
import pms.server.config._
import pms.service.movie.IMDBService
import pms.service.user.UserAccountService

import scala.concurrent.ExecutionContext

/**
  *
  * @author Lorand Szakacs, https://github.com/lorandszakacs
  * @since 11 Jul 2018
  *
  */
final class PMSWeave[F[_]] private (
  serverConfig:         PMSConfig,
  middleware:           AuthMiddleware[F, AuthCtx],
  userAPI:              UserAPI[F],
  movieAPI:             MovieAPI[F],
  httpExecutionContext: ExecutionContext,
)(implicit F:           Async[F], timer: Temporal[F]) {

  def serverResource: Resource[F, Server] =
    BlazeServerBuilder[F](httpExecutionContext)
      .bindHttp(serverConfig.port, serverConfig.host)
      .withHttpApp(http4sApp)
      .withWebSockets(enableWebsockets = false)
      .withBanner(Seq.empty)
      .resource

  private def http4sApp: HttpApp[F] = {
    import org.http4s.implicits.http4sKleisliResponseSyntaxOptionT

//    val routes = NonEmptyList.of[HttpRoutes[F]](userAPI.routes).reduceK
//    val authed = NonEmptyList.of[AuthCtxRoutes[F]](userAPI.authedRoutes, movieAPI.authedRoutes).reduceK
    val pmsAPI: HttpRoutes[F] = ??? //routes <+> middleware(authed)

    org.http4s.server
      .Router[F](("api", pmsAPI))
      .orNotFound
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
      implicit0(random: Random[F]) <- Random.javaUtilConcurrentThreadLocalRandom[F].pure[Resource[F, *]]
      implicit0(logger: Logger[F]) = logging.of(this)
      poolsConfig       <- PMSPoolConfig.resource[F]
      serverConfig      <- PMSConfig.resource[F]
      gmailConfig       <- GmailConfig.resource[F]
      imdbAlgebraConfig <- IMDBAlgebraConfig.resource[F]
      dbConfig          <- DatabaseConfig.resource[F]

      httpServerExecutionContext <-
        PoolFixed.fixed[F]("pms-http4s", maxThreads = poolsConfig.httpServerPool, daemons = false)

      implicit0(transactor: Transactor[F]) <- TransactorAlgebra.resource[F](dbConfig.connection)
      _                          <-
        FlywayAlgebra
          .resource[F](dbConfig.connection)
          .evalMap(flyway => if (dbConfig.forceClean) flyway.cleanDB(logger).map(_ => flyway) else flyway.pure[F])
          .evalMap(flyway => flyway.runMigrations(logger))

      throttler                  <- EffectThrottler.resource[F](imdbAlgebraConfig.requestsInterval, imdbAlgebraConfig.requestsNumber)

      imdbAlgebra    <- IMDBAlgebra.resource[F](throttler)
      authAlgebra    <- UserAuthAlgebra.resource[F]
      accountAlgebra <- UserAccountAlgebra.resource[F]
      userAlgebra    <- UserAlgebra.resource[F]
      movieAlgebra   <- MovieAlgebra.resource[F](authAlgebra)
      emailAlgebra   <- EmailAlgebra.resource[F](gmailConfig)

      imdbService <- IMDBService.resource[F](movieAlgebra, imdbAlgebra)
      userService <- UserAccountService.resource[F](accountAlgebra, emailAlgebra)

      middleware <- AuthedHttp4s.userTokenAuthMiddleware[F](authAlgebra)

      movieAPI <- MovieAPI.resource(imdbService, movieAlgebra)
      userAPI  <- UserAPI.resource(userAlgebra, authAlgebra, userService)

    } yield new PMSWeave[F](serverConfig, middleware, userAPI, movieAPI, httpServerExecutionContext)

}
