package pms.algebra.movie

import pms.algebra.user._

/**
  *
  * @author Lorand Szakacs, https://github.com/lorandszakacs
  * @since 25 Jun 2018
  *
  */
abstract class MovieAlgebra[F[_]] {

  protected def userAuth: UserAuthAlgebra[F]

  final def createMovie(title: MovieTitle, date: Option[ReleaseDate])(implicit auth: AuthCtx): F[Movie] = {
    userAuth.authorize(createMovieImpl(title, date))
  }

  protected def createMovieImpl(title: MovieTitle, date: Option[ReleaseDate]): F[Movie]

  def findMoviesBetween(interval: QueryInterval): F[List[Movie]]
}

object MovieAlgebra {
  import pms.effects._

  def async[F[_]: Async](userAuth: UserAuthAlgebra[F]): MovieAlgebra[F] =
    new impl.AsyncMovieAlgebraImpl(userAuth)
}
