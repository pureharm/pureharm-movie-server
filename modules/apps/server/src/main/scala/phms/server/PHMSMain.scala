package phms.server

import org.http4s.server.Server
import phms._

/** @author Lorand Szakacs, https://github.com/lorandszakacs
  * @since 20 Jun 2018
  */
object PHMSMain extends IOApp {

  override def run(args: List[String]): IO[ExitCode] =
    runResource[IO]
      .use(_ => IO.never)
      .as(ExitCode.Success)

  private def runResource[F[_]](implicit
    F: Async[F]
  ): Resource[F, Server] =
    for {
      weave  <- PHMSWeave.resource[F]
      _      <- Resource.eval(weave.bootstrapServer)
      server <- weave.serverResource
    } yield server

}
