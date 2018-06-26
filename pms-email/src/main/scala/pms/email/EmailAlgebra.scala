package pms.email

import pms.core._

import cats.effect.Async

/**
  *
  * This style of writing algebras (in layman terms: interface) is called
  * "final tagless".
  *
  * @author Lorand Szakacs, https://github.com/lorandszakacs
  * @since 05 Jun 2018
  *
  */
trait EmailAlgebra[F[_]] {

  def sendEmail(to: Email, subject: Subject, content: Content): F[Unit]

}

object EmailAlgebra {

  def gmailClient[F[_]: Async](config: GmailConfig): EmailAlgebra[F] =
    new impl.EmailAlgebraJavaGmailAsyncImpl(config)
}
