package pms.rest.movie

import java.time._
import org.http4s._
import org.http4s.dsl._
import pms.algebra.http._
import pms.algebra.imdb._
import pms.algebra.movie._
import pms._

import pms.service.movie._

/** @author Lorand Szakacs, https://github.com/lorandszakacs
  * @since 25 Jun 2018
  */
final class MovieRestRoutes[F[_]](
  private val imdbService:  IMDBService[F],
  private val movieAlgebra: MovieAlgebra[F],
)(implicit
  val F:                    Async[F]
) extends Http4sDsl[F] with MovieRoutesJSON {

  implicit private val releaseDateQueryParamDecoder: QueryParamDecoder[ReleaseDate] =
    QueryParamDecoder[LocalDate].map(ReleaseDate.apply)

  private object StartReleaseDateQueryMatcher extends QueryParamDecoderMatcher[ReleaseDate]("start")
  private object EndReleaseDateQueryMatcher   extends QueryParamDecoderMatcher[ReleaseDate]("end")

  private object MovieIDMatcher {

    def unapply(str: String): Option[MovieID] =
      if (!str.isEmpty)
        Try(MovieID(str.toLong)).toOption
      else
        None
  }

  implicit private val titleQueryParamDecoder: QueryParamDecoder[TitleQuery] =
    QueryParamDecoder[String].map(TitleQuery.apply)

  private object TitleQueryParamMatcher extends QueryParamDecoderMatcher[TitleQuery]("title")

  private val imdbImportRoutes: AuthCtxRoutes[F] = {
    AuthCtxRoutes[F] { case PUT -> Root / "movie_import" / "imdb" :? TitleQueryParamMatcher(title) as user =>
      Ok(imdbService.scrapeIMDBForTitle(TitleQuery(title))(user))
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
