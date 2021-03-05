package pms.email

import pms.core.{Async, Email}
import pms.core._

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

  def resource[F[_]](config: GmailConfig)(implicit async: Async[F]): Resource[F, EmailAlgebra[F]] =
    Resource.pure[F, EmailAlgebra[F]](new impl.EmailAlgebraJavaGmailAsyncImpl[F](config))
}
