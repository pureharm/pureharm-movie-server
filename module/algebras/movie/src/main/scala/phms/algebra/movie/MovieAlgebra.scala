package phms.algebra.movie

import phms.db._
import phms.algebra.user._
import phms._

/** @author Lorand Szakacs, https://github.com/lorandszakacs
  * @since 25 Jun 2018
  */
trait MovieAlgebra[F[_]] {

  implicit protected def concurrent: Concurrent[F]

  protected def userAuth: UserAuthAlgebra[F]

  final def createMovie(mc: MovieCreation)(implicit auth: AuthCtx): F[Movie] =
    userAuth.authorizeNewbie(createMovieImpl(mc))

  protected def createMovieImpl(mc: MovieCreation): F[Movie]

  def findMoviesBetween(interval: QueryInterval)(implicit auth: AuthCtx): Stream[F, Movie] =
    Stream
      .eval(userAuth.authorizeNewbie(concurrent.unit))
      .flatMap(_ => findMoviesBetweenImpl(interval))

  def fetchMovie(mid: MovieID)(implicit auth: AuthCtx): F[Movie] =
    userAuth.authorizeNewbie(findMovieImpl(mid))

  protected def findMoviesBetweenImpl(interval: QueryInterval): Stream[F, Movie]

  protected def findMovieImpl(mid: MovieID): F[Movie]
}

object MovieAlgebra {
  import phms.algebra.movie.impl.MovieAlgebraImpl

  def resource[F[_]](
    userAuth:   UserAuthAlgebra[F]
  )(implicit F: Concurrent[F], random: Random[F], dbPool: DBPool[F]): Resource[F, MovieAlgebra[F]] =
    new MovieAlgebraImpl(userAuth).pure[Resource[F, *]].widen
}
