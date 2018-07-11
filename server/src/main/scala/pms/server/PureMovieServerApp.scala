package pms.server

import doobie.util.transactor.Transactor
import pms.effects._
import pms.email._
import pms.db.config._
import fs2.{Stream, StreamApp}
import org.http4s._
import org.http4s.server.blaze._

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
      serverConfig <- Stream.eval(PureMovieServerConfig.default[IO])
      gmailConfig  <- Stream.eval(GmailConfig.default[IO])
      dbConfig     <- Stream.eval(DatabaseConfig.default[IO])
      transactor   <- Stream.eval(DatabaseConfigAlgebra.transactor[IO](dbConfig))
      _            <- Stream.eval(DatabaseConfigAlgebra.initializeSQLDb[IO](dbConfig))
      pmsModule    <- Stream.eval(pureMovieServerModule[IO](gmailConfig, transactor))
      exitCode <- serverStream[IO](
                   config  = serverConfig,
                   service = pmsModule.pureMovieServerService
                 )
    } yield exitCode
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
