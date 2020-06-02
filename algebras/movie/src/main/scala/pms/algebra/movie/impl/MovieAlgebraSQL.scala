package pms.algebra.movie.impl

import java.time.LocalDate

import pms.core._
import pms.effects._
import pms.effects.implicits._
import doobie._
import doobie.implicits._
import doobie.implicits.javatime._
import pms.algebra.movie._
import spire.math._

/**
  *
  * @author Lorand Szakacs, https://github.com/lorandszakacs
  * @since 22 Mar 2019
  *
  */
private[movie] object MovieAlgebraSQL {

  //TODO: these metas could be automatically derived for any PhantomType,
  // and should be moved a pms.db module, akin to all other pms-util modules. Currently,
  // an empty such module exists. See what else can/should be moved into that module.
  // an example of how this can be solved, is done here, but for JSON codecs:
  // https://github.com/busymachines/pureharm/blob/master/json-circe/src/main/scala/busymachines/pureharm/internals/json/PureharmJsonInstances.scala#L58
  // and the above bit of code is mixed into our pms.json.implicits; That is why we don't have to do the same thing for JSON codecs.
  implicit protected val movieIDMeta: Meta[MovieID] =
    Meta[Long].imap(MovieID.spook)(MovieID.despook)

  implicit protected val movieTitleMeta: Meta[MovieTitle] =
    Meta[String].imap(MovieTitle.spook)(MovieTitle.despook)

  implicit protected val releaseDateMeta: Meta[ReleaseDate] =
    Meta[LocalDate].imap(ReleaseDate.spook)(ReleaseDate.despook)

  //TODO: the columns for the entire "movies" column can be extracted
  // in a doobie Fragment and reused. Unfortunately stitching together
  // fragments is a bit clunky.
  // Same thing applies for all SQL definitions, for users, authentication, etc.
  // See doobie documentation:
  // https://tpolecat.github.io/doobie/docs/08-Fragments.html
  def findByIDQuery(id: MovieID): ConnectionIO[Option[Movie]] =
    sql"""SELECT id, name, date FROM movies WHERE id=$id""".query[Movie].option

  def fetchByIDQuery(id: MovieID): ConnectionIO[Movie] =
    findByIDQuery(id).flatMap(_.liftTo[ConnectionIO](MovieNotFoundAnomaly(id)))

  private def insertQuery(mc: MovieCreation): ConnectionIO[MovieID] =
    sql"""INSERT INTO movies(name, date) VALUES(${mc.name}, ${mc.date})""".update
      .withUniqueGeneratedKeys[MovieID]("id")

  private def allQuery: ConnectionIO[List[Movie]] =
    sql"""SELECT id, name, date FROM movies""".query[Movie].to[List]

  private def betweenQuery(lower: ReleaseDate, upper: ReleaseDate): ConnectionIO[List[Movie]] =
    sql"""SELECT id, name, date FROM movies WHERE date>=$lower AND date<=$upper"""
      .query[Movie]
      .to[List]

  private def onDateQuery(rd: ReleaseDate): ConnectionIO[List[Movie]] =
    sql"""SELECT id, name, date FROM movies WHERE date=$rd AND date=$rd"""
      .query[Movie]
      .to[List]

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

    case Empty() => List.empty[Movie].pure[ConnectionIO]
  }

  private[movie] def insertMovie(mc: MovieCreation): ConnectionIO[Movie] =
    for {
      id    <- insertQuery(mc)
      movie <- fetchByIDQuery(id).adaptError {
        case NonFatal(e) =>
          Iscata(what = "Failed to fetch movie after insert", where = "insertMovie", Option(e))
      }
    } yield movie

  private def unimplementedInterval[T](str: String): ConnectionIO[T] =
    Fail
      .nicata(s"Unimplemented interval: $str query range")
      .raiseError[ConnectionIO, T]
}
