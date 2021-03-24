package phms.algebra.movie.impl

import phms.db._
import phms.algebra.movie._
import phms.algebra.user.UserAuthAlgebra
import phms._

/** @author Lorand Szakacs, https://github.com/lorandszakacs
  * @since 25 Jun 2018
  */
final private[movie] class MovieAlgebraImpl[F[_]](
  override protected val userAuth: UserAuthAlgebra[F]
)(implicit
  override val concurrent:         Concurrent[F],
  val random:                      Random[F],
  val dbPool:                      DBPool[F],
) extends MovieAlgebra[F] {

  override protected def createMovieImpl(mc: MovieCreation): F[Movie] =
    for {
      movieID <- MovieID.generate[F]
      movie = Movie(movieID, mc.title, mc.date)
      _ <- dbPool.use((session: Session[F]) => PSQLMovies(session).insert(movie))
    } yield movie

  override protected def findMoviesBetweenImpl(interval: QueryInterval): Stream[F, Movie] =
    Stream.resource(dbPool).flatMap((session: Session[F]) => PSQLMovies(session).queryByInterval(interval))

  override protected def findMovieImpl(mid:              MovieID):       F[Movie]         =
    dbPool
      .use(session => PSQLMovies(session).findByID(mid))
      .flatMap(_.liftTo[F](Fail.notFound(s"Movie w/ id: $mid")))

}
