package pms.server.bootstrap

import pms.algebra.user._
import pms.core.Module

/**
  *
  * @author Lorand Szakacs, https://github.com/lorandszakacs
  * @since 13 Jul 2018
  *
  */
trait ModuleServerBootstrap[F[_]] { this: Module[F] with ModuleUserAlgebra[F] with ModuleUserBootstrap[F] =>
  def serverBootstrapAlgebra: F[ServerBootstrapAlgebra[F]] = _serverBootstrapAlgebra

  private lazy val _serverBootstrapAlgebra: F[ServerBootstrapAlgebra[F]] = singleton {
    import cats.implicits._
    for {
      uacc <- userAccountAlgebra
      ubal <- userBootstrapAlgebra
    } yield ServerBootstrapAlgebra.async(uacc, ubal)
  }

}
