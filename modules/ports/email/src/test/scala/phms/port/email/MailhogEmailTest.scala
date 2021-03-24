package phms.port.email

import phms._
import phms.test._
import com.comcast.ip4s._
import phms.kernel._
import phms.logger.Logging

/** This test tries to send emails to a local mailhog server
  * that we startup using the ./docker-mailhog.sh script
  */
final class MailhogEmailTest extends PHMSTest {

  private val emailPort = ResourceFixture[EmailPort[IO]] {
    for {
      supervisor <- Supervisor[IO]
      logging    <- Logging.resource[IO]
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
      )(Sync[IO], supervisor, logging)
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

}
