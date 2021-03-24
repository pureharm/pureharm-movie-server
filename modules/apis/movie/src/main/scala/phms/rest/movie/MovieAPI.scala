package phms.rest.movie

import phms.stack.http.AuthCtxRoutes
import phms.algebra.movie.MovieAlgebra
import phms._
import phms.service.movie.IMDBService

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
