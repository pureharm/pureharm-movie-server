package pms

/** @author Lorand Szakacs, https://github.com/lorandszakacs
  * @since 05 Jun 2018
  */
package object email {

  type EmailSender = String

  type EmailUser     = String
  type EmailPassword = String

  type SmtpHost = String
  type SmtpPort = Int

  type SmtpAuth     = Boolean
  type SmtpStartTLS = Boolean

  type Subject = String
  type Content = String

}
