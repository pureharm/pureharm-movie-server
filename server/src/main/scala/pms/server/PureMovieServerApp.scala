package pms.server

import pms.effects._

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
      server <- Stream.eval(IO.pure(new PureMovieServer[IO]))
      (serverConfig, pmsModule) <- Stream.eval(server.init)
      exitCode <- serverStream[IO](
                   config  = serverConfig,
                   service = pmsModule.pureMovieServerService
                 )
    } yield exitCode
  }

  private def serverStream[F[_]: Effect: Concurrent](
    config:      PureMovieServerConfig,
    service:     HttpService[F]
  )(implicit ec: ExecutionContext): Stream[F, StreamApp.ExitCode] =
    BlazeBuilder[F]
      .bindHttp(config.port, config.host)
      .mountService(service, config.apiRoot)
      .serve
}
