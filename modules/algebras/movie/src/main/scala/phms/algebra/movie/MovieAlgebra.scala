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

package phms.algebra.movie

import phms.db.*
import phms.algebra.user.*
import phms.*

/** @author Lorand Szakacs, https://github.com/lorandszakacs
  * @since 25 Jun 2018
  */
trait MovieAlgebra[F[_]] {

  implicit protected def concurrent: Concurrent[F]

  protected def userAuth: UserAuthAlgebra[F]

  final def createMovie(mc: MovieCreation)(implicit auth: AuthCtx): F[Movie] =
    userAuth.authorizeNewbie(createMovieImpl(mc))

  protected def createMovieImpl(mc: MovieCreation): F[Movie]

  def findMoviesBetween(interval: QueryInterval)(implicit auth: AuthCtx): Stream[F, Movie] =
    Stream
      .eval(userAuth.authorizeNewbie(concurrent.unit))
      .flatMap(_ => findMoviesBetweenImpl(interval))

  def fetchMovie(mid: MovieID)(implicit auth: AuthCtx): F[Movie] =
    userAuth.authorizeNewbie(findMovieImpl(mid))

  protected def findMoviesBetweenImpl(interval: QueryInterval): Stream[F, Movie]

  protected def findMovieImpl(mid: MovieID): F[Movie]
}

object MovieAlgebra {
  import phms.algebra.movie.impl.MovieAlgebraImpl

  def resource[F[_]](
    userAuth:   UserAuthAlgebra[F]
  )(implicit F: Concurrent[F], random: Random[F], dbPool: DBPool[F]): Resource[F, MovieAlgebra[F]] =
    new MovieAlgebraImpl(userAuth).pure[Resource[F, *]].widen
}
