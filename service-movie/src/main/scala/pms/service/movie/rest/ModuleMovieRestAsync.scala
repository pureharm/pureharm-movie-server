package pms.service.movie.rest

import pms.algebra.http._
import pms.algebra.movie._
import pms.service.movie._
import cats.implicits._

/**
  *
  * @author Lorand Szakacs, https://github.com/lorandszakacs
  * @since 27 Jun 2018
  *
  */
trait ModuleMovieRestAsync[F[_]] { this: ModuleMovieServiceAsync[F] with ModuleMovieAsync[F] =>

  def movieRestRoutes: F[MovieRestRoutes[F]] =
    for {
      imdb <- imdbService
    } yield
      new MovieRestRoutes[F](
        imdbService  = imdb,
        movieAlgebra = movieAlgebra
      )

  def movieModuleAuthedRoutes: F[AuthCtxRoutes[F]] = movieRestRoutes.map(_.authedRoutes)

}
