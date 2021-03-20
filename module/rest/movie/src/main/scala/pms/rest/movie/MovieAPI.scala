package pms.rest.movie

import pms.algebra.http.AuthCtxRoutes
import pms.algebra.movie.MovieAlgebra
import pms._
import pms.service.movie.IMDBService

trait MovieAPI[F[_]] {
  def authedRoutes: AuthCtxRoutes[F]
}

object MovieAPI {

  def resource[F[_]: Async](imdbService: IMDBService[F], movieAlgebra: MovieAlgebra[F]): Resource[F, MovieAPI[F]] =
    new MovieRestRoutes[F](
      imdbService  = imdbService,
      movieAlgebra = movieAlgebra,
    ).pure[Resource[F, *]]
      .map(_.authedRoutes)
      .map(rts =>
        new MovieAPI[F] {
          override def authedRoutes: AuthCtxRoutes[F] = rts
        }
      )

}
