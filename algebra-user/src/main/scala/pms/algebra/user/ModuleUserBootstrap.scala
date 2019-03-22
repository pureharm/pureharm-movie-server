package pms.algebra.user

import pms.core.Module
import cats.implicits._

/**
  *
  * Should be used only for development or testing!!!
  *
  * @author Lorand Szakacs, https://github.com/lorandszakacs
  * @since 13 Jul 2018
  *
  */
trait ModuleUserBootstrap[F[_]] { this: Module[F] with ModuleUserAsync[F] =>

  def userBootstrapAlgebra: F[UserAccountBootstrapAlgebra[F]] = _userBootstrapAlgebra

  private lazy val _userBootstrapAlgebra: F[UserAccountBootstrapAlgebra[F]] = singleton {
    userAccountAlgebra.flatMap(a => F.delay(UserAccountBootstrapAlgebra.impl(a)))
  }
}
