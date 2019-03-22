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
trait ModuleMovieServiceAsync[F[_]] { this: Module[F] with ModuleMovieAsync[F] with ModuleIMDBAsync[F] =>

  def imdbService: F[IMDBService[F]] = singleton {
    import cats.implicits._
    for {
      imbd <- imdbAlgebra
      malb <- movieAlgebra
    } yield
      IMDBService.async(
        movieAlgebra = malb,
        imdbAlgebra  = imbd
      )
  }

}
