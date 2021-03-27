/*
 * Copyright 2021 BusyMachines
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package phms.port.email.impl

import phms.*
import phms.kernel.*
import phms.port.email.*
import phms.logger.*

import java.util.Properties
import javax.mail.*
import javax.mail.internet.*

/** Uses javax-mail to send emails. Configured to work only for
  * gmail account:
  * https://javaee.github.io/javamail/Gmail
  *
  * @author Lorand Szakacs, https://github.com/lorandszakacs
  * @since 05 Jun 2018
  */
private[email] class EmailPortJavaxMail[F[_]] private (
  private[this] val config:  GmailConfig,
  private[this] val session: Session,
)(implicit F:                Sync[F], supervisor: Supervisor[F], logging: Logging[F])
  extends EmailPort[F] {

  private val logger:  Logger[F]              = logging.of(this)
  private val loggerR: Logger[Resource[F, *]] = logger.mapK(Resource.liftK)

  override def sendEmail(to: Email, subject: Subject, content: Content)(
    onCompletion:            PartialFunction[OutcomeBackground[F], F[Unit]]
  ): F[Background[F]] = {
    val send = connectedSMTPTransportResource.use { transport =>
      for {
        message <- mimeMessage(to, subject, content)
        _       <- F.blocking(transport.sendMessage(message, message.getAllRecipients))
      } yield ()
    }
    for {
      emailSending <- supervisor.supervise(send)
      _            <- supervisor.supervise {
        for {
          outcome <- emailSending.join
          _       <- outcome match {
            case Outcome.Succeeded(_) => logger.info(s"Sent email to: $to")
            case Outcome.Errored(e)   => logger.warn(e)(s"Failed to send email to: $to")
            case Outcome.Canceled()   => logger.info(s"Cancelled sending email to: $to")
          }
          _       <- onCompletion.applyOrElse(outcome, doNothing)
        } yield ()
      }.void
    } yield emailSending
  }

  private val doNothing: OutcomeBackground[F] => F[Unit] = _ => F.unit

  private def connectedSMTPTransportResource: Resource[F, Transport] =
    for {
      transport <- Resource.make(F.blocking(session.getTransport("smtp")))(transport => F.blocking(transport.close()))
      _         <- Resource.eval(F.blocking(transport.connect(config.host.toString, config.user, config.password)).void)
      _         <- loggerR.debug("Connected to SMTP server")
    } yield transport

  private def mimeMessage(to: Email, subject: Subject, content: Content): F[MimeMessage] = F.delay {
    val message: MimeMessage = new MimeMessage(session)
    message.setFrom(new InternetAddress(config.from))
    message.addRecipient(Message.RecipientType.TO, new InternetAddress(Email.oldType(to)))
    message.setSubject(subject)
    message.setText(content)
    message.saveChanges()
    message
  }
}

private[email] object EmailPortJavaxMail {

  def resource[F[_]](
    config:     GmailConfig
  )(implicit
    F:          Sync[F],
    supervisor: Supervisor[F],
    logging:    Logging[F],
  ): Resource[F, EmailPortJavaxMail[F]] =
    Resource.eval[F, EmailPortJavaxMail[F]] {
      for {
        session <- F.delay {
          /** A complete list of session properties can be found at
            * https://javamail.java.net/nonav/docs/api/com/sun/mail/smtp/package-summary.html
            *
            * session is just a data structure. It can be reused every time.
            */
          val props: Properties = System.getProperties
          props.setProperty("mail.smtp.from", config.from)
          props.put("mail.smtp.host", config.host)
          props.put("mail.smtp.port", config.port.toString)
          props.put("mail.smtp.user", config.user)
          props.put("mail.smtp.password", config.password)
          props.put("mail.smtp.starttls.enable", config.startTLS.toString)
          props.put("mail.smtps.auth", config.auth.toString)
          Session.getInstance(props)
        }
      } yield new EmailPortJavaxMail(config = config, session = session)
    }
}
