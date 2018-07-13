package pms.server.bootstrap

import pms.algebra.user._

/**
  *
  * @author Lorand Szakacs, https://github.com/lorandszakacs
  * @since 13 Jul 2018
  *
  */
trait ModuleServerBootstrap[F[_]] { this: ModuleUserAsync[F] with ModuleUserBootstrap[F] =>
  def serverBootstrapAlgebra: ServerBootstrapAlgebra[F] = _serverBootstrapAlgebra

  private lazy val _serverBootstrapAlgebra: ServerBootstrapAlgebra[F] =
    ServerBootstrapAlgebra.async(userAccountAlgebra, userBootstrapAlgebra)
}
