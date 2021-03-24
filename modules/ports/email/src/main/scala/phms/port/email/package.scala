package phms.port

import phms._
import com.comcast.ip4s.{Host, Port}

/** @author Lorand Szakacs, https://github.com/lorandszakacs
  * @since 05 Jun 2018
  */
package object email {

  type EmailSender = EmailSender.Type
  object EmailSender extends SproutSub[String]

  type EmailUser = EmailUser.Type
  object EmailUser extends SproutSub[String]

  type EmailPassword = EmailPassword.Type
  object EmailPassword extends SproutSub[String]

  type SmtpHost = SmtpHost.Type
  object SmtpHost extends SproutSub[Host]
  type SmtpPort = SmtpPort.Type
  object SmtpPort extends SproutSub[Port]

  type SmtpAuth = SmtpAuth.Type

  object SmtpAuth extends SproutSub[Boolean] {
    val False: SmtpAuth = newType(false)
    val True:  SmtpAuth = newType(true)
  }
  type SmtpStartTLS = SmtpStartTLS.Type

  object SmtpStartTLS extends SproutSub[Boolean] {
    val False: SmtpStartTLS = newType(false)
    val True:  SmtpStartTLS = newType(true)
  }

  type Subject = Subject.Type
  object Subject extends SproutSub[String]
  type Content = Content.Type
  object Content extends SproutSub[String]

}
