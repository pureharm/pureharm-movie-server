package pms.email

/**
  *
  * @author Lorand Szakacs, https://github.com/lorandszakacs
  * @since 05 Jun 2018
  *
  */
final case class GmailConfig(
  from:     Sender,
  user:     EmailUser,
  password: EmailPassword,
  host:     SmtpHost,
  port:     Int,
  auth:     Boolean,
  startTLS: Boolean
)
