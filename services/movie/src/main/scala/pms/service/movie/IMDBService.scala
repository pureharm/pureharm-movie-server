package pms.service.movie

import java.time.LocalDate

import pms.algebra.imdb._
import pms.algebra.movie._
import pms.algebra.user._
import pms.core.Fail
import pms.effects._
import pms.effects.implicits._

/**
  *
  * @author Lorand Szakacs, https://github.com/lorandszakacs
  * @since 25 Jun 2018
  *
  */
final class IMDBService[F[_]] private (
  protected val movieAlgebra: MovieAlgebra[F],
  protected val imdbAlgebra:  IMDBAlgebra[F],
)(implicit F:                 MonadError[F, Throwable]) {

  //TODO: scraping from IMDB can be improved by scraping two pages, first page you do the
  // search query, and then you get the link to your first search result, and gather
  // all information from there. From there you can gather much more, at the cost
  // of two external requests instead of just one.
  def scrapeIMDBForTitle(title:              TitleQuery)(implicit authCtx: AuthCtx): F[Movie] =
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

  /**
    * Unfortunately we only scrape the year from IMDB! :'(
    * This is a first example of what happens when your algebras don't align.
    *
    * Sometimes by poor design, sometimes by physical reality. This time intentionally ;)
    *
    * TODO: Will need overhaul if the above TODOs are implemented
    */
  private def imdbMovieToMovieCreation(imdb: IMDBMovie): MovieCreation =
    MovieCreation(
      name = MovieTitle(IMDBTitle.despook(imdb.title)),
      date = imdb.year.map { y =>
        val year = ReleaseYear.despook(y)
        val ld   = LocalDate.of(year.getValue, 1, 1)
        ReleaseDate(ld)
      },
    )
}

object IMDBService {

  def async[F[_]: Async](movieAlgebra: MovieAlgebra[F], imdbAlgebra: IMDBAlgebra[F]): IMDBService[F] =
    new IMDBService[F](movieAlgebra, imdbAlgebra)
}
