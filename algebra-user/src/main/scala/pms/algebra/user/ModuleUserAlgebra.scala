package pms.algebra.user

import doobie.util.transactor.Transactor
import pms.core.Module

/**
  *
  * Whenever defining these modules, keep all definitions "defs" that way
  * you can easily override them with mocks if the need arises.
  *
  * @author Lorand Szakacs, https://github.com/lorandszakacs
  * @since 21 Jun 2018
  *
  */
trait ModuleUserAlgebra[F[_]] { this: Module[F] =>

  def transactor: Transactor[F]

  def userAlgebra: F[UserAlgebra[F]] = _moduleAlgebra.covary[UserAlgebra[F]]

  def userAccountAlgebra: F[UserAccountAlgebra[F]] = _moduleAlgebra.covary[UserAccountAlgebra[F]]

  def userAuthAlgebra: F[UserAuthAlgebra[F]] = _moduleAlgebra.covary[UserAuthAlgebra[F]]

  def userModuleAlgebra: F[UserModuleAlgebra[F]] = _moduleAlgebra

  private lazy val _moduleAlgebra: F[UserModuleAlgebra[F]] = singleton {
    F.pure(new impl.AsyncAlgebraImpl[F]()(F, F, transactor))
  }
}
