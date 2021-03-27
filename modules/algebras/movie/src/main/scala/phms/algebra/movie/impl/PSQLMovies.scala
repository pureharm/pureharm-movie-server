/*
 * Copyright 2021 BusyMachines
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package phms.algebra.movie.impl

import phms.*
import phms.db.*
import phms.algebra.movie.*

final case class PSQLMovies[F[_]](session: Session[F])(implicit F: MonadCancelThrow[F]) {
  import PSQLMovies.*

  /*_*/
  def insert(m: Movie): F[Unit] =
    session
      .prepare(
        sql"""
           INSERT INTO $movies_table_name ($movies_row)
           VALUES ${movie.values}
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
  import phms.db.codecs.*
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
