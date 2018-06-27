package pms.service.movie.rest

import java.time._

import spire.math.Interval
import cats.implicits._
import doobie.util.transactor.Transactor
import pms.core._
import pms.effects._
import pms.algebra.user._
import pms.algebra.imdb._
import pms.algebra.movie._
import pms.service.movie._
import org.http4s._
import org.http4s.dsl._

/**
  *
  * @author Lorand Szakacs, https://github.com/lorandszakacs
  * @since 25 Jun 2018
  *
  */
final class MovieRestService[F[_]](
  private val imdbService:  IMDBService[F],
  private val movieAlgebra: MovieAlgebra[F],
)(
  implicit val F: Async[F],
  val transactor: Transactor[F]
) extends Http4sDsl[F] with MovieServiceJSON {

  //TODO: move to separate commons class
  implicit private val localDateParamDecoder: QueryParamDecoder[LocalDate] =
    QueryParamDecoder.stringQueryParamDecoder.map(s => LocalDate.parse(s, TimeFormatters.LocalDateFormatter))

  implicit private val releaseDateQueryParamDecoder: QueryParamDecoder[ReleaseDate] =
    localDateParamDecoder.map(ReleaseDate.apply)

  private object StartReleaseDateQueryMatcher extends QueryParamDecoderMatcher[ReleaseDate]("start")
  private object EndReleaseDateQueryMatcher   extends QueryParamDecoderMatcher[ReleaseDate]("end")

  implicit private val titleQueryParamDecoder: QueryParamDecoder[TitleQuery] =
    implicitly[QueryParamDecoder[String]].map(TitleQuery.apply)

  private object TitleQueryParamMatcher extends QueryParamDecoderMatcher[TitleQuery]("title")

  //=======================
  //=======================

  val imdbImportService: HttpService[F] = {
    HttpService[F] {
      case PUT -> Root / "movie_import" / "imdb" :? TitleQueryParamMatcher(title) =>
        Ok(imdbService.scrapeIMDBForTitle(TitleQuery(title))(???))
    }
  }

  //=======================
  //=======================

  val movieService: HttpService[F] = {
    HttpService[F] {
      case req @ POST -> Root / "movie" =>
        for {
          mc   <- req.as[MovieCreation]
          resp <- Created(movieAlgebra.createMovie(mc)(??? : AuthCtx))
        } yield resp

      //=================

      case GET -> Root / "movie" :? StartReleaseDateQueryMatcher(start) :? EndReleaseDateQueryMatcher(end) =>
        val interval = Interval.closed(start, end)
        Ok(movieAlgebra.findMoviesBetween(interval)(??? : AuthCtx))
    }
  }

  //=======================
  //=======================

}
