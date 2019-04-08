package pms.service.movie.rest

import pms.algebra.http._
import pms.algebra.movie._
import pms.service.movie._
import cats.implicits._
import pms.core.Module

/**
  *
  * @author Lorand Szakacs, https://github.com/lorandszakacs
  * @since 27 Jun 2018
  *
  */
trait ModuleMovieRest[F[_]] { this: Module[F] with ModuleMovieService[F] with ModuleMovieAlgebra[F] =>

  def movieModuleAuthedRoutes: F[AuthCtxRoutes[F]] = movieRestRoutes.map(_.authedRoutes)

  private lazy val movieRestRoutes: F[MovieRestRoutes[F]] = singleton {
    for {
      imdb <- imdbService
      malb <- movieAlgebra
    } yield
      new MovieRestRoutes[F](
        imdbService  = imdb,
        movieAlgebra = malb,
      )
  }

}
