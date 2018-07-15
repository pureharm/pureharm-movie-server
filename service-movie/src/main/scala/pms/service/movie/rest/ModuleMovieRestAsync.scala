package pms.service.movie.rest

import pms.algebra.http._
import pms.algebra.movie._
import pms.service.movie._

/**
  *
  * @author Lorand Szakacs, https://github.com/lorandszakacs
  * @since 27 Jun 2018
  *
  */
trait ModuleMovieRestAsync[F[_]] { this: ModuleMovieServiceAsync[F] with ModuleMovieAsync[F] =>

  def movieRestService: MovieRestService[F] = _movieRestService

  def movieModuleAuthedService: AuthCtxService[F] = _movieRestService.authedService

  private lazy val _movieRestService: MovieRestService[F] = new MovieRestService[F](
    imdbService  = imdbService,
    movieAlgebra = movieAlgebra
  )
}
