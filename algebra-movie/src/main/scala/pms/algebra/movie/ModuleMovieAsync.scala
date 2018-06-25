package pms.algebra.movie

import pms.algebra.user.ModuleUserAsync
import pms.effects._

/**
  *
  * @author Lorand Szakacs, https://github.com/lorandszakacs
  * @since 25 Jun 2018
  *
  */
trait ModuleMovieAsync[F[_]] { this: ModuleUserAsync[F] =>

  implicit def async: Async[F]

  def movieAlgebra: MovieAlgebra[F] = _moviesAlgebra

  private lazy val _moviesAlgebra: MovieAlgebra[F] = new impl.AsyncMovieAlgebraImpl[F](
    this.userAuthAlgebra
  )
}
