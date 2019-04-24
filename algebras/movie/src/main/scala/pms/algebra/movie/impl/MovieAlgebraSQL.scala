package pms.algebra.movie.impl

import java.time.LocalDate

import doobie._
import doobie.implicits._

import pms.algebra.movie._
import pms.effects._
import spire.math._

/**
  *
  * @author Lorand Szakacs, https://github.com/lorandszakacs
  * @since 22 Mar 2019
  *
  */
private[movie] object MovieAlgebraSQL {
  private val ME: MonadErrorThr[ConnectionIO] = MonadError.apply[ConnectionIO, Throwable]

  implicit protected val movieIDMeta: Meta[MovieID] =
    Meta[Long].imap(MovieID.spook)(MovieID.despook)

  implicit protected val movieTitleMeta: Meta[MovieTitle] =
    Meta[String].imap(MovieTitle.spook)(MovieTitle.despook)

  implicit protected val releaseDateMeta: Meta[ReleaseDate] =
    Meta[LocalDate].imap(ReleaseDate.spook)(ReleaseDate.despook)

  private def insertQuery(mc: MovieCreation): ConnectionIO[MovieID] =
    sql"""INSERT INTO movies(name, date) VALUES(${mc.name}, ${mc.date})""".update.withUniqueGeneratedKeys[MovieID]("id")

  private def findByIdQuery(id: MovieID): ConnectionIO[Movie] =
    sql"""SELECT id, name, date FROM movies WHERE id=$id""".query[Movie].unique

  private def allQuery: ConnectionIO[List[Movie]] =
    sql"""SELECT id, name, date FROM movies""".query[Movie].to[List]

  private def betweenQuery(lower: ReleaseDate, upper: ReleaseDate): ConnectionIO[List[Movie]] =
    sql"""SELECT id, name, date FROM movies WHERE date>=$lower AND date<=$upper""".query[Movie].to[List]

  private def onDateQuery(rd: ReleaseDate): ConnectionIO[List[Movie]] =
    sql"""SELECT id, name, date FROM movies WHERE date=$rd AND date=$rd""".query[Movie].to[List]

  private[movie] def findBetween(interval: QueryInterval): ConnectionIO[List[Movie]] = interval match {
    case All() => allQuery

    case Above(_, _) => unimplementedInterval("Above")

    case Below(_, _) => unimplementedInterval("Below")

    case Bounded(lower, upper, flags) =>
      flags match {
        case 0 => betweenQuery(lower, upper)
        case f => unimplementedInterval(s"Bounded with non-zero flags: $f")
      }

    case Point(date) => onDateQuery(date)

    case Empty() => ME.pure(List.empty[Movie])
  }

  private[movie] def insertMovie(mc: MovieCreation): ConnectionIO[Movie] =
    for {
      id    <- insertQuery(mc)
      movie <- findByIdQuery(id)
    } yield movie

  private def unimplementedInterval[T](str: String): ConnectionIO[T] =
    ME.raiseError[T](new RuntimeException(s"Unimplemented interval: $str query range"))
}
