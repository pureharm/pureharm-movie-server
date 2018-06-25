package pms.algebra.movie

import pms.algebra.user._

/**
  *
  * @author Lorand Szakacs, https://github.com/lorandszakacs
  * @since 25 Jun 2018
  *
  */
abstract class MovieAlgebra[F[_]](implicit userAuth: UserAuthAlgebra[F]) {

  final def createMovie(name: MovieName, date: Option[ReleaseDate])(implicit auth: AuthCtx): F[Movie] = {
    userAuth.authorize(createMovieImpl(name, date))
  }

  protected def createMovieImpl(name: MovieName, date: Option[ReleaseDate]): F[Movie]

  protected def findMoviesBetween(interval: QueryInterval): F[List[Movie]]
}
