package pms.algebra.movie.impl

import java.time.LocalDate

import doobie._
import doobie.implicits._
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
  val transactor: Transactor[F]
) extends MovieAlgebra[F] {
  import MovieSql._

  override protected def createMovieImpl(mc: MovieCreation): F[Movie] =
    insertMovie(mc).transact(transactor)

  override def findMoviesBetweenImpl(interval: QueryInterval): F[List[Movie]] =
    F.delay(???)

  private def insertMovie(mc: MovieCreation) =
    for {
      id    <- insert(mc)
      movie <- find(id)
    } yield movie
}

object MovieSql {
  implicit val movieIDMeta: Meta[MovieID] = Meta[Long].xmap(
    MovieID.haunt,
    MovieID.exorcise
  )
  implicit val movieTitleMeta: Meta[MovieTitle] = Meta[String].xmap(
    MovieTitle.haunt,
    MovieTitle.exorcise
  )
  implicit val releaseDateMeta: Meta[ReleaseDate] = Meta[LocalDate].xmap(
    ReleaseDate.haunt,
    ReleaseDate.exorcise
  )

  def insert(mc: MovieCreation): ConnectionIO[MovieID] = mc.date match {
    case Some(date) =>
      sql"""INSERT INTO movies(name, date) VALUES(${mc.name}, $date})""".update.withUniqueGeneratedKeys[MovieID]("id")
    case None => sql"""INSERT INTO movies(name) VALUES(${mc.name}""".update.withUniqueGeneratedKeys[MovieID]("id")
  }

  def lastId(): ConnectionIO[MovieID] = sql"""SELECT lastval()""".query[MovieID].unique

  def find(id: MovieID): ConnectionIO[Movie] =
    sql"""SELECT id, name, date FROM movies WHERE id=$id""".query[Movie].unique
}
