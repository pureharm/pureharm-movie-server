package pms.server

import busymachines.pureharm.effects.pools.Pools
import cats.effect.IO._
import cats.effect.{ContextShift, IO}
import org.http4s.server.Server
import org.http4s.server.blaze._
import pms.effects._
import pms.logger.PMSLogger
import pms.server.config.PMSPoolConfig

/**
  *
  * @author Lorand Szakacs, https://github.com/lorandszakacs
  * @since 20 Jun 2018
  *
  */
object PMSMain extends PureharmIOApp {

  override val ioRuntime: Later[(ContextShift[IO], Timer[IO])] = IORuntime.defaultMainRuntime(s"main-pure-movie-server")
  implicit def cs:        ContextShift[IO]                     = this.contextShift
  implicit def tm:        Timer[IO]                            = this.timer

  override def run(args: List[String]): IO[ExitCode] =
    serverResource
      .use(_ => IO.never)
      .as(ExitCode.Success)

  private def serverResource[F[_]: ConcurrentEffect](implicit
    timer:        Timer[F],
    contextShift: ContextShift[F],
  ): Resource[F, Server[F]] =
    for {
      poolsConfig                <- PMSPoolConfig.defaultR[F]
      httpServerExecutionContext <- Pools.fixed[F](maxThreads = poolsConfig.httpServerPool)
      dbExecutionContext         <- Pools.fixed[F](maxThreads = poolsConfig.pgSqlPool)

      server <- PMSWeave.resource[F](PMSLogger.getLogger[F])

      (serverConfig, httpService) <- server.initialise(contextShift, dbExecutionContext)(timer)

      server <- BlazeServerBuilder[F](httpServerExecutionContext)
        .bindHttp(serverConfig.port, serverConfig.host)
        .withHttpApp(httpService)
        .withWebSockets(enableWebsockets = true)
        .withBanner(Seq.empty)
        .resource

    } yield server

}
