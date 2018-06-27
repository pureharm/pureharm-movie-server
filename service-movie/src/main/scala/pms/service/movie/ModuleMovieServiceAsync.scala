package pms.service.movie

import pms.algebra.movie._
import pms.algebra.imdb._

/**
  *
  * @author Lorand Szakacs, https://github.com/lorandszakacs
  * @since 27 Jun 2018
  *
  */
trait ModuleMovieServiceAsync[F[_]] { this: ModuleMovieAsync[F] with ModuleIMDBAsync[F] =>

  def imdbService: IMDBService[F] = _imdbService

  private lazy val _imdbService: IMDBService[F] = IMDBService.async(
    movieAlgebra = movieAlgebra,
    imdbAlgebra  = imdbAlgebra
  )

}
