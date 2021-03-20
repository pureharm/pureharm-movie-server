package pms.server

import org.http4s.server.Server
import pms._

/** @author Lorand Szakacs, https://github.com/lorandszakacs
  * @since 20 Jun 2018
  */
object PMSMain extends IOApp {

  override def run(args: List[String]): IO[ExitCode] =
    runResource[IO]
      .use(_ => IO.never)
      .as(ExitCode.Success)

  private def runResource[F[_]](implicit
    CE: Async[F]
  ): Resource[F, Server] =
    for {
      weave  <- PMSWeave.resource[F]
      server <- weave.serverResource
    } yield server

}
