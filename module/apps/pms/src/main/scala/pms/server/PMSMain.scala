package pms.server

import cats.effect.{ContextShift, IO}
import org.http4s.server.Server
import pms.core._

/**
  *
  * @author Lorand Szakacs, https://github.com/lorandszakacs
  * @since 20 Jun 2018
  *
  */
object PMSMain extends IOApp {

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
      weave  <- PMSWeave.resource[F]
      server <- weave.serverResource
    } yield server

}
