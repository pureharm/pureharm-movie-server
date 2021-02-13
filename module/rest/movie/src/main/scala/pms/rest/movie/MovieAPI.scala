package pms.rest.movie

import pms.algebra.http.AuthCtxRoutes
import pms.algebra.movie.MovieAlgebra
import pms.effects._
import pms.service.movie.IMDBService

trait MovieAPI[F[_]] {
  def authedRoutes: AuthCtxRoutes[F]
}

object MovieAPI {

  def resource[F[_]: Async](imdbService: IMDBService[F], movieAlgebra: MovieAlgebra[F]): Resource[F, MovieAPI[F]] =
    Resource
      .pure(
        new MovieRestRoutes[F](
          imdbService  = imdbService,
          movieAlgebra = movieAlgebra,
        )
      )
      .map(_.authedRoutes)
      .map(rts =>
        new MovieAPI[F] {
          override def authedRoutes: AuthCtxRoutes[F] = rts
        }
      )

}
