package pms.server

import cats.effect.ContextShift
import org.http4s.server.blaze.BlazeServerBuilder
import org.http4s.server.{AuthMiddleware, Server}
import org.http4s.{HttpApp, HttpRoutes}
import pms.algebra.http.{AuthCtxRoutes, AuthedHttp4s}
import pms.algebra.imdb.{IMDBAlgebra, IMDBAlgebraConfig}
import pms.algebra.movie.MovieAlgebra
import pms.algebra.user.{AuthCtx, UserAccountAlgebra, UserAlgebra, UserAuthAlgebra}
import pms.db.config._
import pms.db.{FlywayAlgebra, TransactorAlgebra}
import pms.email._
import pms.logger._
import pms.core._
import pms.effects.EffectThrottler
import pms.rest.movie.MovieAPI
import pms.rest.user.UserAPI
import pms.server.config.PMSConfig
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
  serverConfig:           PMSConfig,
  middleware:             AuthMiddleware[F, AuthCtx],
  userAPI:                UserAPI[F],
  movieAPI:               MovieAPI[F],
)(implicit private val F: Concurrent[F]) {

  def serverResource(implicit
    concurrentEffect:     ConcurrentEffect[F],
    timer:                Timer[F],
    httpExecutionContext: ExecutionContext,
  ): Resource[F, Server] =
    BlazeServerBuilder[F](httpExecutionContext)
      .bindHttp(serverConfig.port, serverConfig.host)
      .withHttpApp(http4sApp)
      .withWebSockets(enableWebsockets = false)
      .withBanner(Seq.empty)
      .resource

  private def http4sApp(implicit F: Sync[F]): HttpApp[F] = {
    import org.http4s.implicits.http4sKleisliResponseSyntaxOptionT

    val routes = NonEmptyList.of[HttpRoutes[F]](userAPI.routes).reduceK
    val authed = NonEmptyList.of[AuthCtxRoutes[F]](userAPI.authedRoutes, movieAPI.authedRoutes).reduceK
    val pmsAPI: HttpRoutes[F] = routes <+> middleware(authed)

    org.http4s.server
      .Router[F](("api", pmsAPI))
      .orNotFound
  }

}

object PMSWeave {

  def resource[F[_]](
    logger:             Logger[F],
    dbExecutionContext: ExecutionContext,
  )(implicit timer:     Timer[F], mainContextShift: ContextShift[F], C: Concurrent[F]): Resource[F, PMSWeave[F]] =
    for {
      serverConfig      <- PMSConfig.defaultR[F]
      gmailConfig       <- GmailConfig.defaultR[F]
      imdbAlgebraConfig <- IMDBAlgebraConfig.defaultR[F]
      dbConfig          <- DatabaseConfig.defaultR[F]

      transactor <- TransactorAlgebra.resource[F](dbExecutionContext, dbConfig.connection)
      _          <-
        FlywayAlgebra
          .resource[F](dbConfig.connection)
          .evalMap(flyway => if (dbConfig.forceClean) flyway.cleanDB(logger).map(_ => flyway) else Sync[F].pure(flyway))
          .evalMap(flyway => flyway.runMigrations(logger))

      throttler  <- EffectThrottler.resource[F](imdbAlgebraConfig.requestsInterval, imdbAlgebraConfig.requestsNumber)

      imdbAlgebra    <- IMDBAlgebra.resource[F](throttler)
      authAlgebra    <- UserAuthAlgebra.resource[F](transactor, C)
      accountAlgebra <- UserAccountAlgebra.resource[F](transactor, C)
      userAlgebra    <- UserAlgebra.resource[F](transactor, C)
      movieAlgebra   <- MovieAlgebra.resource[F](authAlgebra)(transactor, C)
      emailAlgebra   <- EmailAlgebra.resource[F](gmailConfig)

      imdbService <- IMDBService.resource[F](movieAlgebra, imdbAlgebra)
      userService <- UserAccountService.resource[F](accountAlgebra, emailAlgebra)

      middleware <- AuthedHttp4s.userTokenAuthMiddleware[F](authAlgebra)

      movieAPI <- MovieAPI.resource(imdbService, movieAlgebra)
      userAPI  <- UserAPI.resource(userAlgebra, authAlgebra, userService)

    } yield new PMSWeave[F](serverConfig, middleware, userAPI, movieAPI)

}
