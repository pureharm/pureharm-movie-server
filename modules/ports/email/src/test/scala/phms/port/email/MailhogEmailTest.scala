package phms.port.email

import phms.*
import phms.test.*
import com.comcast.ip4s.*
import phms.kernel.*
import phms.logger.Logging

/** This test tries to send emails to a local mailhog server
  * that we startup using the ./docker-mailhog.sh script
  */
final class MailhogEmailTest extends PHMSTest {

  private val emailPort = ResourceFixture[EmailPort[IO]] {
    for {
      given Supervisor[IO] <- Supervisor[IO]
      given Logging[IO]    <- Logging.resource[IO]
      emailPort  <- EmailPort.resource[IO](
        GmailConfig(
          from     = EmailSender("phms-testing@example.com"),
          user     = EmailUser("phms-testing@example.com"),
          password = EmailPassword("not-required"),
          host     = SmtpHost(host"localhost"),
          port     = SmtpPort(port"10125"),
          auth     = SmtpAuth.False,
          startTLS = SmtpStartTLS.False,
        )
      )
    } yield emailPort
  }

  emailPort.test("send email to locally started mailhog server") { emailPort =>
    for {
      to         <- Email[IO]("phms-mailhog-test@example.com")
      background <- emailPort.sendEmail(to, Subject("subject"), Content("content")) { case _ => IO.unit }
      outcome    <- background.join
      _ = outcome match {
        case Outcome.Errored(e)   => fail("failed to send email", e)
        case Outcome.Canceled()   => fail("email sending was cancelled")
        case Outcome.Succeeded(_) => assert(cond = true)
      }
    } yield ()
  }

  emailPort.test(TestOptions("send out 1000 emails in parallel").tag(Slow)) { emailPort =>
    for {
      to <- Email[IO]("phms-mailhog-test@example.com")
      _  <- Stream
        .range[IO, Int](0, 1000)
        .parEvalMapUnordered(Runtime.getRuntime.availableProcessors()) { idx =>
          for {
            background <- emailPort.sendEmail(to, Subject(s"subject -- $idx"), Content(s"content $idx")) { case _ =>
              IO.unit
            }
            outcome    <- background.join
            _ = outcome match {
              case Outcome.Errored(e)   => fail("failed to send email", e)
              case Outcome.Canceled()   => fail("email sending was cancelled")
              case Outcome.Succeeded(_) => assert(cond = true)
            }
          } yield ()
        }
        .compile
        .drain
    } yield ()
  }

}
