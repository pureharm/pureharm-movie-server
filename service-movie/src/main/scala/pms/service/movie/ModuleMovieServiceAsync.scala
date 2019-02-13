package pms.service.movie

import pms.algebra.movie._
import pms.algebra.imdb._
import cats.implicits._

/**
  *
  * @author Lorand Szakacs, https://github.com/lorandszakacs
  * @since 27 Jun 2018
  *
  */
trait ModuleMovieServiceAsync[F[_]] { this: ModuleMovieAsync[F] with ModuleIMDBAsync[F] =>

  def imdbService: F[IMDBService[F]] =
    for {
      imbd <- imdbAlgebra
    } yield
      IMDBService.async(
        movieAlgebra = movieAlgebra,
        imdbAlgebra  = imbd
      )

}
