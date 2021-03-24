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

package phms.api.movie

import phms.stack.http.AuthCtxRoutes
import phms.algebra.movie.MovieAlgebra
import phms._
import phms.organizer.movie.IMDBOrganizer

trait MovieAPI[F[_]] {
  def authedRoutes: AuthCtxRoutes[F]
}

object MovieAPI {

  def resource[F[_]](imdbOrganizer: IMDBOrganizer[F], movieAlgebra: MovieAlgebra[F])(implicit
    F:                              Concurrent[F],
    D:                              Defer[F],
  ): Resource[F, MovieAPI[F]] =
    new MovieRoutes[F](
      imdbOrganizer = imdbOrganizer,
      movieAlgebra  = movieAlgebra,
    ).pure[Resource[F, *]]
      .map(_.authedRoutes)
      .map(rts =>
        new MovieAPI[F] {
          override def authedRoutes: AuthCtxRoutes[F] = rts
        }
      )

}
