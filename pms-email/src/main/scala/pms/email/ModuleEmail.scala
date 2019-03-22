package pms.email

import pms.core.Module

/**
  *
  * @author Lorand Szakacs, https://github.com/lorandszakacs
  * @since 27 Jun 2018
  *
  */
trait ModuleEmail[F[_]] { this: Module[F] =>

  def gmailConfig: GmailConfig

  def emailAlgebra: F[EmailAlgebra[F]] = _emailAlgebra

  private lazy val _emailAlgebra: F[EmailAlgebra[F]] = singleton {
    F.delay(new impl.EmailAlgebraJavaGmailAsyncImpl[F](gmailConfig))
  }

}
