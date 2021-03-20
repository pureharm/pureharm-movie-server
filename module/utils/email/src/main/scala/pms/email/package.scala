package pms

/** @author Lorand Szakacs, https://github.com/lorandszakacs
  * @since 05 Jun 2018
  */
package object email {
  //TODO: make PhantomTypes out of these
  type Sender = String

  type EmailUser     = String
  type EmailPassword = String

  type SmtpHost = String

  type Subject = String
  type Content = String

}
