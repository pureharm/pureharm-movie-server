package pms.algebra.movie

import doobie.util.transactor.Transactor
import pms.algebra.user._

/**
  *
  * @author Lorand Szakacs, https://github.com/lorandszakacs
  * @since 25 Jun 2018
  *
  */
abstract class MovieAlgebra[F[_]] {

  protected def userAuth: UserAuthAlgebra[F]

  final def createMovie(mc: MovieCreation)(implicit auth: AuthCtx): F[Movie] =
    userAuth.authorize(createMovieImpl(mc))

  protected def createMovieImpl(mc: MovieCreation): F[Movie]

  def findMoviesBetween(interval: QueryInterval)(implicit auth: AuthCtx): F[List[Movie]] =
    userAuth.authorize(findMoviesBetweenImpl(interval))

  protected def findMoviesBetweenImpl(interval: QueryInterval): F[List[Movie]]
}

object MovieAlgebra {
  import pms.effects._

  def async[F[_]: Async](userAuth: UserAuthAlgebra[F])(implicit transactor: Transactor[F]): MovieAlgebra[F] =
    new impl.AsyncMovieAlgebraImpl(userAuth)
}
