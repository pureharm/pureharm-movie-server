package pms.server

import cats.effect.{ConcurrentEffect, ExitCode, IOApp, Timer}
import pms.effects._
import fs2.Stream
import org.http4s._
import org.http4s.server.blaze._

/**
  *
  * @author Lorand Szakacs, https://github.com/lorandszakacs
  * @since 20 Jun 2018
  *
  */
object PureMovieServerApp extends IOApp {

  override def run(args: List[String]): IO[ExitCode] = {
    implicit val sch: Scheduler = Scheduler.global
    for {
      server <- PureMovieServer.concurrent[IO]
      (serverConfig, pmsModule) <- server.init
      exitCode <- serverStream[IO](
                   config  = serverConfig,
                   service = pmsModule.pureMovieServerService
                 ).compile.lastOrError
    } yield exitCode
  }

  private def serverStream[F[_]: ConcurrentEffect: Timer](
    config:  PureMovieServerConfig,
    service: HttpService[F]
  ): Stream[F, ExitCode] =
    BlazeBuilder[F]
      .bindHttp(config.port, config.host)
      .mountService(service, config.apiRoot)
      .serve
}
