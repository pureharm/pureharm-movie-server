package pms.algebra.user

import pms.effects._

/**
  *
  * Whenever defining these modules, keep all definitions "defs" that way
  * you can easily override them with mocks if the need arises.
  *
  * @author Lorand Szakacs, https://github.com/lorandszakacs
  * @since 21 Jun 2018
  *
  */
trait ModuleUserAsync[F[_]] {

  implicit def async: Async[F]

  def userAlgebra: UserAlgebra[F] = _moduleAlgebra

  def userAccountAlgebra: UserAccountAlgebra[F] = _moduleAlgebra

  def userAuthAlgebra: UserAuthAlgebra[F] = _moduleAlgebra

  def userModuleAlgebra: UserModuleAlgebra[F] = _moduleAlgebra

  private lazy val _moduleAlgebra: UserModuleAlgebra[F] = new impl.AsyncAlgebraImpl[F]()
}
