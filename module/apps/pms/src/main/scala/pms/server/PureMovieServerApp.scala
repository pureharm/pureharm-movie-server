package pms.server

import pms.effects._
import fs2.Stream
import org.http4s._
import org.http4s.server.blaze._
import org.http4s.implicits._
import org.http4s.server.Router
import pms.server.config.PureMovieServerConfig

/**
  *
  * @author Lorand Szakacs, https://github.com/lorandszakacs
  * @since 20 Jun 2018
  *
  */
object PureMovieServerApp extends PureharmIOApp {


  override val ioRuntime: Later[(ContextShift[IO], Timer[IO])] =
    IORuntime.defaultMainRuntime(s"main-pure-movie-server")

  override def run(args: List[String]): IO[ExitCode] =
    for {
      //FIXME: pass in proper context shifts and everything
      // see scaladoc of PureharmIOApp and use that instead of IOApp to instantiate runtime explicitly:
      // https://github.com/busymachines/pureharm/blob/master/effects-cats/src/main/scala/busymachines/pureharm/effects/pools/IORuntime.scala#L27
      // then read the scaladoc of Pools what pool is appropriate.
      // https://github.com/busymachines/pureharm/blob/master/effects-cats/src/main/scala/busymachines/pureharm/effects/pools/Pools.scala#L29
      // cases that need to be considered:
      // 1) connection pool for Doobie — fixed thread pool
      // 2) transaction pool for Doobie — cached thread pool
      // 3) thread pool for http4s BlazeServer to run requests on — should be fixed size, to add backpressure to your app.
      // 4) cached thread pool for blocking external requests, put behind a BlockingShifter: https://github.com/busymachines/pureharm/blob/master/effects-cats/src/main/scala/busymachines/pureharm/internals/effects/BlockingShifter.scala#L24
      //    which should be used to run any external calls, like scrapping IMDB, sending emails, and amazon interactions
      server                    <- PureMovieServer.concurrent[IO](timer, contextShift)
      (serverConfig, pmsModule) <- server.init
      routes                    <- pmsModule.pureMovieServerRoutes
      exitCode                  <- serverStream[IO](
        config = serverConfig,
        routes = routes,
      ).compile.lastOrError
    } yield exitCode

  private def serverStream[F[_]: ConcurrentEffect: Timer](
    config: PureMovieServerConfig,
    routes: HttpRoutes[F],
  ): Stream[F, ExitCode] = {
    val httpApp = Router(config.apiRoot -> routes).orNotFound
    BlazeServerBuilder[F](ExecutionContext.global) //FIXME: pass in proper EC — see above todo
      .bindHttp(config.port, config.host)
      .withHttpApp(httpApp)
      .serve
      .covary
  }

}
