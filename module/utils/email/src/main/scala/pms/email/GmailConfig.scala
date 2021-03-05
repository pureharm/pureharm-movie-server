package pms.email

import pms.core._
import pms.config._

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
  startTLS: Boolean,
)

object GmailConfig extends ConfigLoader[GmailConfig] {
  implicit override def configReader: ConfigReader[GmailConfig] = semiauto.deriveReader[GmailConfig]
  override def default[F[_]: Sync]: F[GmailConfig] = this.load[F]("pms.email.gmail")
}
