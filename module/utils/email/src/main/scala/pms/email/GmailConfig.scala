package pms.email

import pms._
import pms.config._

/** @author Lorand Szakacs, https://github.com/lorandszakacs
  * @since 05 Jun 2018
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

object GmailConfig {
  def resource[F[_]: Config]: Resource[F, GmailConfig] = ???
}
