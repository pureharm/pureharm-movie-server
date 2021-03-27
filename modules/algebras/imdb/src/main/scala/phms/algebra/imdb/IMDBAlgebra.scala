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

package phms.algebra.imdb

import phms.algebra.imdb.impl.{IMDBAlgebraImpl, JsoupWrapper}
import phms._

/** @author Lorand Szakacs, https://github.com/lorandszakacs
  * @since 25 Jun 2018
  */
trait IMDBAlgebra[F[_]] {
  def scrapeMovieByTitle(title: TitleQuery): F[Option[IMDBMovie]]
}

object IMDBAlgebra {
  def resource[F[_]](throttler: EffectThrottler[F])(implicit F: Sync[F]): Resource[F, IMDBAlgebra[F]] =
    for {
      jsoup <- JsoupWrapper.resource[F]
    } yield new IMDBAlgebraImpl[F](throttler, jsoup)

}
