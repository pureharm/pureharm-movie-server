package phms.email

/** @author Lorand Szakacs, https://github.com/lorandszakacs
  * @since 05 Jun 2018
  */
final case class GmailConfig(
  from:     EmailSender,
  user:     EmailUser,
  password: EmailPassword,
  host:     SmtpHost,
  port:     SmtpPort,
  auth:     SmtpAuth,
  startTLS: SmtpStartTLS,
)
