package phms.algebra.movie.impl

import phms.db._
import phms.algebra.movie._
import phms.algebra.user.UserAuthAlgebra
import phms._

/** @author Lorand Szakacs, https://github.com/lorandszakacs
  * @since 25 Jun 2018
  */
final private[movie] class MovieAlgebraImpl[F[_]: MonadThrow](
  override protected val userAuth: UserAuthAlgebra[F],
  private val dbPool:              DDPool[F],
) extends MovieAlgebra[F] {

  override protected def createMovieImpl(mc: MovieCreation): F[Movie] = ???

  override protected def findMoviesBetweenImpl(interval: QueryInterval): F[List[Movie]] = ???

  override protected def findMovieImpl(mid: MovieID): F[Movie] = ???
}
