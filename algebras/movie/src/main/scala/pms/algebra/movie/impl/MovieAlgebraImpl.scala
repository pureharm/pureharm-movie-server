package pms.algebra.movie.impl

import doobie._
import doobie.implicits._

import pms.effects._

import pms.algebra.movie._
import pms.algebra.user.UserAuthAlgebra

/**
  *
  * @author Lorand Szakacs, https://github.com/lorandszakacs
  * @since 25 Jun 2018
  *
  */
final private[movie] class MovieAlgebraImpl[F[_]: BracketThr] private (
  private val transactor:          Transactor[F],
  override protected val userAuth: UserAuthAlgebra[F],
) extends MovieAlgebra[F] {

  override protected def createMovieImpl(mc: MovieCreation): F[Movie] =
    MovieAlgebraSQL.insertMovie(mc).transact(transactor)

  override protected def findMoviesBetweenImpl(interval: QueryInterval): F[List[Movie]] =
    MovieAlgebraSQL.findBetween(interval).transact(transactor)

  override protected def findMovieImpl(mid: MovieID): F[Movie] =
    MovieAlgebraSQL.fetchByIDQuery(mid).transact(transactor)
}

private[movie] object MovieAlgebraImpl {

  def bracket[F[_]: BracketThr](userAuth: UserAuthAlgebra[F], transactor: Transactor[F]) =
    new MovieAlgebraImpl(transactor, userAuth)
}
