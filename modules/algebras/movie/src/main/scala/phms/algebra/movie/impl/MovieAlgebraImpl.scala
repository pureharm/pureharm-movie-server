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

import phms.db.*
import phms.algebra.movie.*
import phms.algebra.user.UserAuthAlgebra
import phms.*

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
