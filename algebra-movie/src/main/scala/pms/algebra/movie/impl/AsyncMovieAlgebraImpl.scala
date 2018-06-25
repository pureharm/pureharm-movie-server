package pms.algebra.movie.impl

import pms.algebra.movie._
import pms.algebra.user.UserAuthAlgebra
import pms.effects.Async

/**
  *
  * @author Lorand Szakacs, https://github.com/lorandszakacs
  * @since 25 Jun 2018
  *
  */
final private[movie] class AsyncMovieAlgebraImpl[F[_]](
  override protected val userAuth: UserAuthAlgebra[F]
)(
  implicit val F: Async[F],
) extends MovieAlgebra[F] {

  override protected def createMovieImpl(mc: MovieCreation): F[Movie] =
    F.delay(???)

  override def findMoviesBetweenImpl(interval: QueryInterval): F[List[Movie]] =
    F.delay(???)
}
