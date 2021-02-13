package pms.server

import busymachines.pureharm.effects.pools.Pools
import cats.effect.ContextShift
import doobie.Transactor
import org.http4s.implicits.http4sKleisliResponseSyntaxOptionT
import org.http4s.server.blaze.BlazeServerBuilder
import org.http4s.server.{AuthMiddleware, Server}
import org.http4s.{HttpApp, HttpRoutes}
import pms.algebra.http.{AuthCtxRoutes, AuthedHttp4s}
import pms.algebra.imdb.{IMDBAlgebra, IMDBAlgebraConfig}
import pms.algebra.movie.MovieAlgebra
import pms.algebra.user.{AuthCtx, UserAccountAlgebra, UserAlgebra, UserAuthAlgebra}
import pms.db.config._
import pms.db.{FlywayAlgebra, TransactorAlgebra}
import pms.effects._
import pms.email._
import pms.logger._
import pms.core._
import pms.rest.movie.MovieAPI
import pms.rest.user.UserAPI
import pms.server.config.{PMSConfig, PMSPoolConfig}
import pms.service.movie.IMDBService
import pms.service.user.UserAccountService
import scala.concurrent.ExecutionContext

/**
  *
  * @author Lorand Szakacs, https://github.com/lorandszakacs
  * @since 11 Jul 2018
  *
  */
final class PMSWeave[F[_]: Concurrent] private (
  serverConfig:           PMSConfig,
  flyway:                 FlywayAlgebra[F],
  middleware:             AuthMiddleware[F, AuthCtx],
  userAPI:                UserAPI[F],
  movieAPI:               MovieAPI[F],
  logger:                 PMSLogger[F],
)(implicit private val F: Concurrent[F]) {

  private def serverResource[F[_]: ConcurrentEffect](implicit
    timer:                Timer[F],
    httpExecutionContext: ExecutionContext,
  ): Resource[F, Server[F]] =
    BlazeServerBuilder[F](httpExecutionContext)
      .bindHttp(serverConfig.port, serverConfig.host)
      .withHttpApp(http4sApp)
      .withWebSockets(enableWebsockets = true)
      .withBanner(Seq.empty)
      .resource

  private def http4sApp(implicit F: Sync[F]): HttpApp[F] = {
    val routes = NonEmptyList.of[HttpRoutes[F]](userAPI.routes).reduceK
    val authed = NonEmptyList.of[AuthCtxRoutes[F]](userAPI.authedRoutes, movieAPI.authedRoutes).reduceK
    val pmsAPI: HttpRoutes[F] = routes <+> middleware(authed)

    org.http4s.server.Router[F](
        ("api", pmsAPI)
      ).orNotFound
    }


  def initialise(
    mainContextShift:   ContextShift[F],
    dbExecutionContext: ExecutionContext,
  )(implicit timer:     Timer[F]): Resource[F, (PMSConfig, HttpApp[F])] =
    for {
      serverConfig      <- PMSConfig.defaultR[F]
      gmailConfig       <- GmailConfig.defaultR[F]
      imdbAlgebraConfig <- IMDBAlgebraConfig.defaultR[F]
      dbConfig          <- DatabaseConfig.defaultR[F]

      transactor: Transactor[F] <-
        TransactorAlgebra.resource(dbExecutionContext, dbConfig.connection)(F, mainContextShift)
      flyway <- FlywayAlgebra.resource(dbConfig.connection)

      throttler <- EffectThrottler.resource(imdbAlgebraConfig.requestsInterval, imdbAlgebraConfig.requestsNumber)

      imdbAlgebra    <- IMDBAlgebra.resource(throttler)
      authAlgebra    <- UserAuthAlgebra.resource(transactor)
      accountAlgebra <- UserAccountAlgebra.resource(transactor)
      userAlgebra    <- UserAlgebra.resource(transactor)
      movieAlgebra   <- MovieAlgebra.resource(authAlgebra)(transactor)
      emailAlgebra   <- EmailAlgebra.resource(gmailConfig)

      imdbService <- IMDBService.resource(movieAlgebra, imdbAlgebra)
      userService <- UserAccountService.resource(accountAlgebra, emailAlgebra)

      movieAPI <- MovieAPI.resource(imdbService, movieAlgebra)
      userAPI  <- UserAPI.resource(userAlgebra, authAlgebra, userService)

      middleware <- AuthedHttp4s.userTokenAuthMiddleware[F](authAlgebra)
//      nrOfMigs   <- DatabaseConfigAlgebra.initializeSQLDb[F](dbConfig)
//      _          <- logger.info(s"Successfully ran #$nrOfMigs migrations")
      pmsModule  <- moduleInit(
        gmailConfig,
        imdbAlgebraConfig,
        bootstrap = serverConfig.bootstrap,
      )(transactor, timer)
      _          <- logger.info(s"Successfully initialized pure-movie-server")
    } yield (serverConfig, pmsModule)

  private def moduleInit(
    gmailConfig:      GmailConfig,
    imdblgebraConfig: IMDBAlgebraConfig,
    bootstrap:        Boolean,
  )(implicit
    transactor:       Transactor[F],
    timer:            Timer[F],
  ): F[ModulePureMovieServer[F]] =
    if (bootstrap) {
      //TODO: add better logic for handling boostrap being applied twice
      // important aspects to consider:
      //  1) give good diagnostics of what specifically went wrong so that the developer knows what's up
      //  2) distinguish between recoverable errors in bootstrap, and non-recoverable errors
      logger.warn(
        "BOOTSTRAP — initializing server in bootstrap mode — if this is on prod, you seriously botched this one"
      ) >> ModulePureMovieServerBootstrap
        .concurrent(gmailConfig, imdblgebraConfig)
        .flatTap(_.bootstrap)
        .widen[ModulePureMovieServer[F]]
    }
    else ModulePureMovieServer.concurrent(gmailConfig, imdblgebraConfig)

}

object PMSWeave {

  def resource[F[_]: Concurrent](
    logger:             PMSLogger[F],
    mainContextShift:   ContextShift[F],
    dbExecutionContext: ExecutionContext,
  )(implicit timer:     Timer[F]): Resource[F, PMSWeave[F]] =
    for {
      serverConfig      <- PMSConfig.defaultR[F]
      gmailConfig       <- GmailConfig.defaultR[F]
      imdbAlgebraConfig <- IMDBAlgebraConfig.defaultR[F]
      dbConfig          <- DatabaseConfig.defaultR[F]

      transactor: Transactor[F] <- TransactorAlgebra.resource[F](dbExecutionContext, dbConfig.connection)(mainContextShift)
      flyway <- FlywayAlgebra.resource[F](dbConfig.connection)

      throttler <- EffectThrottler.resource[F](imdbAlgebraConfig.requestsInterval, imdbAlgebraConfig.requestsNumber)

      imdbAlgebra    <- IMDBAlgebra.resource[F](throttler)
      authAlgebra    <- UserAuthAlgebra.resource[F](transactor)
      accountAlgebra <- UserAccountAlgebra.resource[F](transactor)
      userAlgebra    <- UserAlgebra.resource[F](transactor)
      movieAlgebra   <- MovieAlgebra.resource[F](authAlgebra)(transactor)
      emailAlgebra   <- EmailAlgebra.resource[F](gmailConfig)

      imdbService <- IMDBService.resource[F](movieAlgebra, imdbAlgebra)
      userService <- UserAccountService.resource[F](accountAlgebra, emailAlgebra)

      middleware <- AuthedHttp4s.userTokenAuthMiddleware[F](authAlgebra)

      movieAPI <- MovieAPI.resource(imdbService, movieAlgebra)
      userAPI  <- UserAPI.resource(userAlgebra, authAlgebra, userService)
    } yield new PMSWeave[F](serverConfig, flyway, middleware, userAPI, movieAPI, logger)

}
