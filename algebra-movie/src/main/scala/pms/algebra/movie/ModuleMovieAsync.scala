package pms.algebra.movie

import doobie.util.transactor.Transactor
import pms.algebra.user.ModuleUserAsync
import pms.core.Module

/**
  *
  * @author Lorand Szakacs, https://github.com/lorandszakacs
  * @since 25 Jun 2018
  *
  */
trait ModuleMovieAsync[F[_]] { this: Module[F] with ModuleUserAsync[F] =>

  override def transactor: Transactor[F]

  def movieAlgebra: F[MovieAlgebra[F]] = _movieAlgebra

  private lazy val _movieAlgebra = singleton {
    import cats.implicits._
    userAuthAlgebra.flatMap(uaa => MovieAlgebra.async[F](uaa)(F, transactor))
  }
}
