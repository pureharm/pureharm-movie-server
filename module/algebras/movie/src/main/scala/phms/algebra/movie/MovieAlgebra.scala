package phms.algebra.movie

import phms.db._
import phms.algebra.user._
import phms._

/** @author Lorand Szakacs, https://github.com/lorandszakacs
  * @since 25 Jun 2018
  */
trait MovieAlgebra[F[_]] {

  protected def userAuth: UserAuthAlgebra[F]

  final def createMovie(mc: MovieCreation)(implicit auth: AuthCtx): F[Movie] =
    userAuth.authorizeNewbie(createMovieImpl(mc))

  protected def createMovieImpl(mc: MovieCreation): F[Movie]

  def findMoviesBetween(interval: QueryInterval)(implicit auth: AuthCtx): F[List[Movie]] =
    userAuth.authorizeNewbie(findMoviesBetweenImpl(interval))

  def fetchMovie(mid: MovieID)(implicit auth: AuthCtx): F[Movie] =
    userAuth.authorizeNewbie(findMovieImpl(mid))

  protected def findMoviesBetweenImpl(interval: QueryInterval): F[List[Movie]]

  protected def findMovieImpl(mid: MovieID): F[Movie]
}

object MovieAlgebra {
  import phms.algebra.movie.impl.MovieAlgebraImpl

  def resource[F[_]](
    userAuth:            UserAuthAlgebra[F]
  )(implicit dbPool: DDPool[F], F: cats.effect.MonadCancelThrow[F]): Resource[F, MovieAlgebra[F]] =
    new MovieAlgebraImpl(userAuth, dbPool).pure[Resource[F, *]].widen
}
