package phms.api.movie

import java.time._
import org.http4s._
import org.http4s.dsl._
import phms.stack.http._
import phms.algebra.imdb._
import phms.algebra.movie._
import phms._

import phms.organizer.movie._

/** @author Lorand Szakacs, https://github.com/lorandszakacs
  * @since 25 Jun 2018
  */
final class MovieRoutes[F[_]](
  private val imdbOrganizer: IMDBOrganizer[F],
  private val movieAlgebra:  MovieAlgebra[F],
)(implicit
  val F:                     Concurrent[F],
  val D:                     Defer[F],
) extends Http4sDsl[F] with MovieRoutesJSON {

  implicit private val releaseDateQueryParamDecoder: QueryParamDecoder[ReleaseDate] =
    QueryParamDecoder[LocalDate].map(ReleaseDate.apply)

  private object StartReleaseDateQueryMatcher extends QueryParamDecoderMatcher[ReleaseDate]("start")
  private object EndReleaseDateQueryMatcher   extends QueryParamDecoderMatcher[ReleaseDate]("end")

  private object MovieIDMatcher {
    def unapply(str: String): Option[MovieID] = MovieID.fromString[Try](str).toOption
  }

  implicit private val titleQueryParamDecoder: QueryParamDecoder[TitleQuery] =
    QueryParamDecoder[String].map(TitleQuery.apply)

  private object TitleQueryParamMatcher extends QueryParamDecoderMatcher[TitleQuery]("title")

  private val imdbImportRoutes: AuthCtxRoutes[F] = {
    AuthCtxRoutes[F] { case PUT -> Root / "movie_import" / "imdb" :? TitleQueryParamMatcher(title) as user =>
      Ok(imdbOrganizer.scrapeIMDBForTitle(TitleQuery(title))(user))
    }
  }

  private val movieRoutes: AuthCtxRoutes[F] = {
    AuthCtxRoutes[F] {
      case (req @ POST -> Root / "movie") as user =>
        for {
          mc   <- req.as[MovieCreation]
          resp <- Created(movieAlgebra.createMovie(mc)(user))
        } yield resp

      case GET -> Root / "movie" / MovieIDMatcher(mid) as user =>
        Ok(movieAlgebra.fetchMovie(mid)(user))

      case GET -> Root / "movie" :? StartReleaseDateQueryMatcher(start) :? EndReleaseDateQueryMatcher(end) as user =>
        val interval = (start, end)
        Ok(movieAlgebra.findMoviesBetween(interval)(user))
    }
  }

  val authedRoutes: AuthCtxRoutes[F] = imdbImportRoutes <+> movieRoutes

}
