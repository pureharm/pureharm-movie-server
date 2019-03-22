package pms.algebra.movie

import doobie.util.transactor.Transactor
import pms.algebra.user.ModuleUserAlgebra
import pms.core.Module

/**
  *
  * @author Lorand Szakacs, https://github.com/lorandszakacs
  * @since 25 Jun 2018
  *
  */
trait ModuleMovieAlgebra[F[_]] { this: Module[F] with ModuleUserAlgebra[F] =>

  override def transactor: Transactor[F]

  def movieAlgebra: F[MovieAlgebra[F]] = _movieAlgebra

  private lazy val _movieAlgebra = singleton {
    import cats.implicits._
    userAuthAlgebra.flatMap(uaa => MovieAlgebra.async[F](uaa)(F, transactor))
  }
}
