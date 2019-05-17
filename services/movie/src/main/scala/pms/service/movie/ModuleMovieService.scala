package pms.service.movie

import pms.algebra.movie._
import pms.algebra.imdb._
import pms.core.Module

/**
  *
  * @author Lorand Szakacs, https://github.com/lorandszakacs
  * @since 27 Jun 2018
  *
  */
trait ModuleMovieService[F[_]] {
  this: Module[F] with ModuleMovieAlgebra[F] with ModuleIMDBAlgebra[F] =>

  def imdbService: F[IMDBService[F]] = _imdbService

  private lazy val _imdbService: F[IMDBService[F]] = singleton {
    import pms.effects.implicits._
    for {
      imbd <- imdbAlgebra
      malb <- movieAlgebra
    } yield
      IMDBService.async(
        movieAlgebra = malb,
        imdbAlgebra = imbd,
      )
  }

}
