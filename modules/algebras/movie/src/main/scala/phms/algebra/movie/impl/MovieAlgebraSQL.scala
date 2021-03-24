//package phms.algebra.movie.impl
//
//import java.time.LocalDate
//import phms.db._
//import phms.algebra.movie._
//import phms.core._
//import spire.math._
//
///**
//  *
//  * @author Lorand Szakacs, https://github.com/lorandszakacs
//  * @since 22 Mar 2019
//  *
//  */
//private[movie] object MovieAlgebraSQL {
//
//  implicit protected val movieIDMeta:     Meta[MovieID]     = Meta[Long].haunt[MovieID]
//  implicit protected val movieTitleMeta:  Meta[MovieTitle]  = Meta[String].haunt[MovieTitle]
//  implicit protected val releaseDateMeta: Meta[ReleaseDate] = Meta[LocalDate].haunt[ReleaseDate]
//
//  //TODO: the columns for the entire "movies" column can be extracted
//  // in a doobie Fragment and reused. Unfortunately stitching together
//  // fragments is a bit clunky.
//  // Same thing applies for all SQL definitions, for users, authentication, etc.
//  // See doobie documentation:
//  // https://tpolecat.github.io/doobie/docs/08-Fragments.html
//  def findByIDQuery(id: MovieID): ConnectionIO[Option[Movie]] =
//    sql"""SELECT id, name, date FROM movies WHERE id=$id""".query[Movie].option
//
//  def fetchByIDQuery(id: MovieID): ConnectionIO[Movie] =
//    findByIDQuery(id).flatMap(_.liftTo[ConnectionIO](MovieNotFoundAnomaly(id)))
//
//  private def insertQuery(mc: MovieCreation): ConnectionIO[MovieID] =
//    sql"""INSERT INTO movies(name, date) VALUES(${mc.name}, ${mc.date})""".update
//      .withUniqueGeneratedKeys[MovieID]("id")
//
//  private def allQuery: ConnectionIO[List[Movie]] =
//    sql"""SELECT id, name, date FROM movies""".query[Movie].to[List]
//
//  private def betweenQuery(lower: ReleaseDate, upper: ReleaseDate): ConnectionIO[List[Movie]] =
//    sql"""SELECT id, name, date FROM movies WHERE date>=$lower AND date<=$upper"""
//      .query[Movie]
//      .to[List]
//
//  private def onDateQuery(rd: ReleaseDate): ConnectionIO[List[Movie]] =
//    sql"""SELECT id, name, date FROM movies WHERE date=$rd AND date=$rd"""
//      .query[Movie]
//      .to[List]
//
//  private[movie] def findBetween(interval: QueryInterval): ConnectionIO[List[Movie]] = interval match {
//    case All() => allQuery
//
//    case Above(_, _) => unimplementedInterval("Above")
//
//    case Below(_, _) => unimplementedInterval("Below")
//
//    case Bounded(lower, upper, flags) =>
//      flags match {
//        case 0 => betweenQuery(lower, upper)
//        case f => unimplementedInterval(s"Bounded with non-zero flags: $f")
//      }
//
//    case Point(date) => onDateQuery(date)
//
//    case Empty() => List.empty[Movie].pure[ConnectionIO]
//  }
//
//  private[movie] def insertMovie(mc: MovieCreation): ConnectionIO[Movie] =
//    for {
//      id    <- insertQuery(mc)
//      movie <- fetchByIDQuery(id).adaptError {
//        case NonFatal(e) =>
//          Iscata(what = "Failed to fetch movie after insert", where = "insertMovie", Option(e))
//      }
//    } yield movie
//
//  private def unimplementedInterval[T](str: String): ConnectionIO[T] =
//    Fail
//      .nicata(s"Unimplemented interval: $str query range")
//      .raiseError[ConnectionIO, T]
//}
