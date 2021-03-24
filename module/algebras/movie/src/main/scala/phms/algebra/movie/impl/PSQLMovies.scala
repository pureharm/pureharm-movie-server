package phms.algebra.movie.impl

import phms._
import phms.db._
import phms.algebra.movie._

final case class PSQLMovies[F[_]](session: Session[F])(implicit F: MonadCancelThrow[F]) {
  import PSQLMovies._

  /*_*/
  def insert(m: Movie): F[Unit] =
    session
      .prepare(
        sql"""
           INSERT INTO $movies_table_name
           VALUES ($movies_row)
           ${movie.values}
         """.command: Command[Movie]
      )
      .use(_.execute(m).void)

  def findByID(mid: MovieID): F[Option[Movie]] =
    session
      .prepare(
        sql"""
           SELECT $movies_row
           FROM $movies_table_name
           WHERE $id = $uuid_movie_id
         """.query(movie): Query[MovieID, Movie]
      )
      .use(_.option(mid))

  def queryByInterval(r: QueryInterval): Stream[F, Movie] = {
    val start = r._1
    val end   = r._2
    Stream
      .resource(
        session.prepare(
          sql"""
           SELECT $movies_row
           FROM $movies_table_name
           WHERE $release_date IS NOT NULl AND
             $release_date >= $date_release_date
             AND $release_date <= $date_release_date
            """.query(movie): Query[ReleaseDate ~ ReleaseDate, Movie]
        )
      )
      .flatMap(_.stream(start ~ end, 10))
  }
  /*_*/

}

object PSQLMovies {
  import phms.db.codecs._
  /*_*/
  private val id:           Column = const"id"
  private val title:        Column = const"title"
  private val release_date: Column = const"release_date"

  private val uuid_movie_id:     Codec[MovieID]     = uuid.sprout[MovieID]
  private val varchar_title:     Codec[MovieTitle]  = varchar.sprout[MovieTitle]
  private val date_release_date: Codec[ReleaseDate] = date.sprout[ReleaseDate]

  private val movies_row:        Row       = sql"""$id, $title, $release_date"""
  private val movies_table_name: TableName = const"movies"

  private val movie: Codec[Movie] = (uuid_movie_id ~ varchar_title ~ date_release_date.opt).gimap[Movie]
  /*_*/
}
