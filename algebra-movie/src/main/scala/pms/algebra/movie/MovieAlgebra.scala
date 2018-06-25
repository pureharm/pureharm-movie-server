package pms.algebra.movie

import pms.algebra.user._

/**
  *
  * @author Lorand Szakacs, https://github.com/lorandszakacs
  * @since 25 Jun 2018
  *
  */
abstract class MovieAlgebra[F[_]](implicit userAuth: UserAuthAlgebra[F]) {

  final def createMovie(title: MovieTitle, date: Option[ReleaseDate])(implicit auth: AuthCtx): F[Movie] = {
    userAuth.authorize(createMovieImpl(title, date))
  }

  protected def createMovieImpl(title: MovieTitle, date: Option[ReleaseDate]): F[Movie]

  protected def findMoviesBetween(interval: QueryInterval): F[List[Movie]]
}

object MovieAlgebra {
  import pms.effects._

  def async[F[_]: Async](implicit userAuth: UserAuthAlgebra[F]): MovieAlgebra[F] = ???
}
