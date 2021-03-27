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

package phms.organizer.movie

import java.time.LocalDate

import phms.algebra.imdb.*
import phms.algebra.movie.*
import phms.algebra.user.*
import phms.Fail
import phms.*

/** @author Lorand Szakacs, https://github.com/lorandszakacs
  * @since 25 Jun 2018
  */
final class IMDBOrganizer[F[_]] private (
  protected val movieAlgebra: MovieAlgebra[F],
  protected val imdbAlgebra:  IMDBAlgebra[F],
)(implicit F:                 MonadThrow[F]) {

  //TODO: scraping from IMDB can be improved by scraping two pages, first page you do the
  // search query, and then you get the link to your first search result, and gather
  // all information from there. From there you can gather much more, at the cost
  // of two external requests instead of just one.
  def scrapeIMDBForTitle(title: TitleQuery)(implicit authCtx: AuthCtx): F[Movie] =
    for {
      maybe: Option[IMDBMovie] <- imdbAlgebra.scrapeMovieByTitle(title)
      //TODO: this is a fairly common shape transformation, F[Option[A]] into F[B],
      // with error in case of None, and a function from A => B
      // find out of the box one, either from cats or pureharm-effects
      toCreate <- maybe match {
        case None        =>
          F.raiseError[MovieCreation](Fail.invalid(s"Could not find imdb movie with title: $title"))
        case Some(value) => F.pure[MovieCreation](imdbMovieToMovieCreation(value))
      }
      movie    <- movieAlgebra.createMovie(toCreate)
    } yield movie

  /** Unfortunately we only scrape the year from IMDB! :'(
    * This is a first example of what happens when your algebras don't align.
    *
    * Sometimes by poor design, sometimes by physical reality. This time intentionally ;)
    *
    * TODO: Will need overhaul if the above TODOs are implemented
    */
  private def imdbMovieToMovieCreation(imdb: IMDBMovie): MovieCreation =
    MovieCreation(
      title = MovieTitle(IMDBTitle.oldType(imdb.title)),
      date  = imdb.year.map { y =>
        val year = ReleaseYear.oldType(y)
        val ld   = LocalDate.of(year.getValue, 1, 1)
        ReleaseDate(ld)
      },
    )
}

object IMDBOrganizer {

  def resource[F[_]](movieAlgebra: MovieAlgebra[F], imdbAlgebra: IMDBAlgebra[F])(implicit
    F:                             MonadThrow[F]
  ): Resource[F, IMDBOrganizer[F]] =
    Resource.pure[F, IMDBOrganizer[F]](new IMDBOrganizer[F](movieAlgebra, imdbAlgebra))
}
