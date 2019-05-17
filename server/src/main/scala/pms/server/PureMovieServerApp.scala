package pms.server

import pms.effects._
import fs2.Stream
import org.http4s._
import org.http4s.server.blaze._
import org.http4s.implicits._
import org.http4s.server.Router

/**
  *
  * @author Lorand Szakacs, https://github.com/lorandszakacs
  * @since 20 Jun 2018
  *
  */
object PureMovieServerApp extends IOApp {

  override def run(args: List[String]): IO[ExitCode] = {
    for {
      server <- PureMovieServer
        .concurrent[IO](timer, contextShift) //FIXME: pass in proper context shift to do DB IO
      (serverConfig, pmsModule) <- server.init
      routes <- pmsModule.pureMovieServerRoutes
      exitCode <- serverStream[IO](
        config = serverConfig,
        routes = routes,
      ).compile.lastOrError
    } yield exitCode
  }

  private def serverStream[F[_]: ConcurrentEffect: Timer](
      config: PureMovieServerConfig,
      routes: HttpRoutes[F],
  ): Stream[F, ExitCode] = {
    val httpApp = Router(config.apiRoot -> routes).orNotFound
    BlazeServerBuilder[F]
      .bindHttp(config.port, config.host)
      .withHttpApp(httpApp)
      .serve
      .covary
  }

}
