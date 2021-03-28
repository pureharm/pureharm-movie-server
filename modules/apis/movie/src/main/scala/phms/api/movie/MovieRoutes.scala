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

import java.time.*
import org.http4s.*
import org.http4s.dsl.{*, given}
import phms.stack.http.{*, given}
import phms.algebra.imdb.*
import phms.algebra.movie.*
import phms.*

import phms.organizer.movie.*

/** @author Lorand Szakacs, https://github.com/lorandszakacs
  * @since 25 Jun 2018
  */
final class MovieRoutes[F[_]](
  private val imdbOrganizer: IMDBOrganizer[F],
  private val movieAlgebra:  MovieAlgebra[F],
)(using
  val F:                     Concurrent[F],
  val D:                     Defer[F],
) extends Http4sDsl[F] with MovieRoutesJSON {

  private given releaseDateQueryParamDecoder: QueryParamDecoder[ReleaseDate] =
    QueryParamDecoder[LocalDate].map(ReleaseDate.apply)

  private object StartReleaseDateQueryMatcher extends QueryParamDecoderMatcher[ReleaseDate]("start")
  private object EndReleaseDateQueryMatcher   extends QueryParamDecoderMatcher[ReleaseDate]("end")

  private object MovieIDMatcher {
    def unapply(str: String): Option[MovieID] = MovieID.fromString[Try](str).toOption
  }

  private given titleQueryParamDecoder: QueryParamDecoder[TitleQuery] =
    QueryParamDecoder[String].map(TitleQuery.apply)

  private object TitleQueryParamMatcher extends QueryParamDecoderMatcher[TitleQuery]("title")

  private val imdbImportRoutes: AuthCtxRoutes[F] = {
    AuthCtxRoutes[F] { case PUT -> Root / "movie_import" / "imdb" :? TitleQueryParamMatcher(title) `as` user =>
      Ok(imdbOrganizer.scrapeIMDBForTitle(TitleQuery(title))(using user))
    }
  }

  private val movieRoutes: AuthCtxRoutes[F] = {
    AuthCtxRoutes[F] {
      case (req @ POST -> Root / "movie") `as` user =>
        for {
          mc   <- req.as[MovieCreation]
          resp <- Created(movieAlgebra.createMovie(mc)(using user))
        } yield resp

      case GET -> Root / "movie" / MovieIDMatcher(mid) `as` user =>
        Ok(movieAlgebra.fetchMovie(mid)(using user))

      case GET -> Root / "movie" :? StartReleaseDateQueryMatcher(start) :? EndReleaseDateQueryMatcher(end) `as` user =>
        val interval = (start, end)
        Ok(movieAlgebra.findMoviesBetween(interval)(using user))
    }
  }

  val authedRoutes: AuthCtxRoutes[F] = imdbImportRoutes <+> movieRoutes

}
