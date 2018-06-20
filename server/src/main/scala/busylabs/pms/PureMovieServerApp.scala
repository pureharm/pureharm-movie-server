package busylabs.pms

import busymachines.effects._
import cats.effect.Effect
import fs2.{Stream, StreamApp}
import org.http4s.HttpService

/**
  *
  * @author Lorand Szakacs, https://github.com/lorandszakacs
  * @since 20 Jun 2018
  *
  */
object PureMovieServerApp extends StreamApp[IO] {
  import org.http4s.server.blaze.BlazeBuilder

  private def pureMovieHttpServices[F[_]: Effect]: HttpService[F] =
    new HelloWorldService().service

  private def serverStream[F[_]: Effect](
    config:      PureMovieServerConfig
  )(implicit ec: ExecutionContext): Stream[F, StreamApp.ExitCode] =
    BlazeBuilder[F]
      .bindHttp(config.port, config.host)
      .mountService(pureMovieHttpServices, config.apiRoot)
      .serve

  override def stream(args: List[String], requestShutdown: IO[Unit]): Stream[IO, StreamApp.ExitCode] = {
    implicit val sch: Scheduler = Scheduler.global
    for {
      config   <- Stream.eval(PureMovieServerConfig.default[IO])
      exitCode <- serverStream[IO](config)
    } yield exitCode
  }
}
