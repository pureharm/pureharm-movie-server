package pms.server

import cats.implicits._

import pms.effects._
import pms.email._
import pms.db.config._

import fs2.{Stream, StreamApp}
import org.http4s._
import org.http4s.server.blaze._

import io.chrisdavenport.log4cats.slf4j.Slf4jLogger
import doobie.util.transactor.Transactor

/**
  *
  * @author Lorand Szakacs, https://github.com/lorandszakacs
  * @since 20 Jun 2018
  *
  */
object PureMovieServerApp extends StreamApp[IO] {

  override def stream(args: List[String], requestShutdown: IO[Unit]): Stream[IO, StreamApp.ExitCode] = {
    implicit val sch: Scheduler = Scheduler.global
    for {
      (serverConfig, pmsModule) <- Stream.eval(serverInit[IO])
      exitCode <- serverStream[IO](
                   config  = serverConfig,
                   service = pmsModule.pureMovieServerService
                 )
    } yield exitCode
  }

  private def serverInit[F[_]: Concurrent]: F[(PureMovieServerConfig, ModulePureMovieServer[F])] = {
    for {
      logger       <- Slf4jLogger.create[F]
      serverConfig <- PureMovieServerConfig.default[F]
      gmailConfig  <- GmailConfig.default[F]
      dbConfig     <- DatabaseConfig.default[F]
      transactor   <- DatabaseConfigAlgebra.transactor[F](dbConfig)
      nrOfMigs     <- DatabaseConfigAlgebra.initializeSQLDb[F](dbConfig)
      _            <- logger.info(s"Successfully ran #$nrOfMigs migrations")
      pmsModule    <- pureMovieServerModule[F](gmailConfig, transactor)
      _            <- logger.info(s"Successfully initialized pure-movie-server")
    } yield (serverConfig, pmsModule)
  }

  private def pureMovieServerModule[F[_]: Concurrent](
    gmailConfig: GmailConfig,
    transactor:  Transactor[F]
  ): F[ModulePureMovieServer[F]] =
    Concurrent.apply[F].delay(ModulePureMovieServer.concurrent(gmailConfig)(implicitly, transactor))

  private def serverStream[F[_]: Effect: Concurrent](
    config:      PureMovieServerConfig,
    service:     HttpService[F]
  )(implicit ec: ExecutionContext): Stream[F, StreamApp.ExitCode] =
    BlazeBuilder[F]
      .bindHttp(config.port, config.host)
      .mountService(service, config.apiRoot)
      .serve
}
