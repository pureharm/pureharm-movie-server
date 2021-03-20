package pms.email.impl

import java.util.Properties
import javax.mail._
import javax.mail.internet._
import pms._
import pms.email._
import pms.logger._

/**
  *
  * Uses javax-mail to send emails. Configured to work only for
  * gmail account:
  * https://javaee.github.io/javamail/Gmail
  *
  * @author Lorand Szakacs, https://github.com/lorandszakacs
  * @since 05 Jun 2018
  *
  */
private[email] class EmailAlgebraJavaGmailAsyncImpl[F[_]: Async](
  private val config: GmailConfig
) extends EmailAlgebra[F] {

  private val F: Async[F] = Async.apply[F]

  private val logger: Logger[F] = Logger.getLogger[F]

  override def sendEmail(to:        Email, subject: Subject, content: Content): F[Unit] = {
    val mimaMessage = F.pure {
      val message: MimeMessage = new MimeMessage(session)

      message.setFrom(new InternetAddress(config.from))
      message.addRecipient(Message.RecipientType.TO, new InternetAddress(to.plainTextEmail))
      message.setSubject(subject)
      message.setText(content)
      message.saveChanges()
      message
    }

    for {
      message   <- mimaMessage
      transport <- F.delay(session.getTransport("smtp"))
      _         <-
        F.delay(transport.connect(config.host, config.user, config.password))
          .onError(cleanupErr(transport))
      _         <- logger.info("Connected to SMTP server")
      _         <-
        F.delay(transport.sendMessage(message, message.getAllRecipients))
          .onError(cleanupErr(transport))
      _         <- logger.info(s"Sent email to: ${to.plainTextEmail}")
      _         <- cleanup(transport)
    } yield ()
  }

  /**
    * WTB bracket, please :( but we're on an older version of cats-effect
    * because Monix has not been updated to 1.0.0-RC2 yet
    *
    * https://typelevel.org/cats-effect/typeclasses/bracket.html
    * This would make this whole thing super trivial.
    */
  private def cleanupErr(transport: Transport): PartialFunction[Throwable, F[Unit]] = {
    case scala.util.control.NonFatal(e) =>
      logger.warn(e)("Failed to send email.") >>
        cleanup(transport)
  }

  private def cleanup(transport: Transport): F[Unit] =
    F.delay(transport.close())

  /**
    * A complete list of session properties can be found at
    * https://javamail.java.net/nonav/docs/api/com/sun/mail/smtp/package-summary.html
    *
    * session is just a data structure. It can be reused every time.
    */
  private[this] lazy val session: Session = {
    val props: Properties = System.getProperties

    props.setProperty("mail.smtp.from", config.from)

    props.put("mail.smtp.host", config.host)
    props.put("mail.smtp.port", config.port.toString)
    props.put("mail.smtp.user", config.user)
    props.put("mail.smtp.password", config.password)
    props.put("mail.smtp.starttls.enable", config.startTLS.toString)
    props.put("mail.smtps.auth", config.auth.toString)

    Session.getInstance(props, null)
  }

}
