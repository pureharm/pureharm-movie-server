package pms.algebra.user

/**
  *
  * Should be used only for development or testing!!!
  *
  * @author Lorand Szakacs, https://github.com/lorandszakacs
  * @since 13 Jul 2018
  *
  */
trait ModuleUserBootstrap[F[_]] { this: ModuleUserAsync[F] =>

  def userBootstrapAlgebra: UserAccountBootstrapAlgebra[F] = _userBootstrapAlgebra

  private lazy val _userBootstrapAlgebra: UserAccountBootstrapAlgebra[F] =
    UserAccountBootstrapAlgebra.impl(userAccountAlgebra)
}
