package phms.rest.movie

import phms.stack.http.AuthCtxRoutes
import phms.algebra.movie.MovieAlgebra
import phms._
import phms.organizer.movie.IMDBOrganizer

trait MovieAPI[F[_]] {
  def authedRoutes: AuthCtxRoutes[F]
}

object MovieAPI {

  def resource[F[_]: Async](imdbOrganizer: IMDBOrganizer[F], movieAlgebra: MovieAlgebra[F]): Resource[F, MovieAPI[F]] =
    new MovieRestRoutes[F](
      imdbOrganizer = imdbOrganizer,
      movieAlgebra  = movieAlgebra,
    ).pure[Resource[F, *]]
      .map(_.authedRoutes)
      .map(rts =>
        new MovieAPI[F] {
          override def authedRoutes: AuthCtxRoutes[F] = rts
        }
      )

}
