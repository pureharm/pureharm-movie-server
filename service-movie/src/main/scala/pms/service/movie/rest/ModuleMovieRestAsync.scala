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

  def movieRestService: F[MovieRestService[F]] =
    for {
      imdb <- imdbService
    } yield
      new MovieRestService[F](
        imdbService  = imdb,
        movieAlgebra = movieAlgebra
      )

  def movieModuleAuthedService: F[AuthCtxService[F]] = movieRestService.map(_.authedService)

}
