package pms.email

import pms.effects.Async

/**
  *
  * @author Lorand Szakacs, https://github.com/lorandszakacs
  * @since 27 Jun 2018
  *
  */
trait ModuleEmailASync[F[_]] {

  implicit def async: Async[F]

  def config: GmailConfig

  def emailAlgebra: EmailAlgebra[F] = _emailAlgebra

  private lazy val _emailAlgebra = new impl.EmailAlgebraJavaGmailAsyncImpl[F](config)

}
