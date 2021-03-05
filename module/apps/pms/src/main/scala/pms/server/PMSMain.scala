package pms.server

import busymachines.pureharm.effects.pools.Pools
import cats.effect.{ContextShift, IO}
import org.http4s.server.Server
import pms.core._
import pms.logger.Logger
import pms.server.config.PMSPoolConfig

/**
  *
  * @author Lorand Szakacs, https://github.com/lorandszakacs
  * @since 20 Jun 2018
  *
  */
object PMSMain extends PureharmIOApp {

  override val ioRuntime: Later[(ContextShift[IO], Timer[IO])] = IORuntime.defaultMainRuntime(s"main-pure-movie-server")

  override def run(args: List[String]): IO[ExitCode] =
    runResource[IO](this.timer, this.contextShift, IO.ioConcurrentEffect(this.contextShift))
      .use(_ => IO.never)
      .as(ExitCode.Success)

  private def runResource[F[_]](implicit
    timer: Timer[F],
    CS:    ContextShift[F],
    CE:    ConcurrentEffect[F],
  ): Resource[F, Server] =
    for {
      poolsConfig                <- PMSPoolConfig.defaultR[F]
      httpServerExecutionContext <- Pools.fixed[F](maxThreads = poolsConfig.httpServerPool)
      dbExecutionContext         <- Pools.fixed[F](maxThreads = poolsConfig.pgSqlPool)
      weave                      <- PMSWeave.resource[F](Logger.getLogger[F], dbExecutionContext)
      server                     <- weave.serverResource(CE, timer, httpServerExecutionContext)
    } yield server

}
