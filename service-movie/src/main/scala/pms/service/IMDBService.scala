package pms.service

import java.time.LocalDate

import pms.effects._
import cats.implicits._
import busymachines.core._
import pms.algebra.user._
import pms.algebra.imdb._
import pms.algebra.movie._

/**
  *
  * @author Lorand Szakacs, https://github.com/lorandszakacs
  * @since 25 Jun 2018
  *
  */
abstract class IMDBService[F[_]](
  protected val movieAlgebra: MovieAlgebra[F],
  protected val imdbAlgebra:  IMDBAlgebra[F]
)(
  implicit F: MonadError[F, Throwable]
) {

  def scrapeIMDBForTitle(title: TitleQuery)(implicit authCtx: AuthCtx): F[Movie] =
    for {
      maybe <- imdbAlgebra.scrapeMovieByTitle(title)
      //TODO: write abstract combinators
      toCreate <- maybe match {
                   case None        => F.raiseError(InvalidInputFailure(s"Could not find imdb movie with title: $title"))
                   case Some(value) => F.pure(imdbMovieToMovieCreation(value))
                 }
      movie <- movieAlgebra.createMovie(toCreate)
    } yield movie

  /**
    * Unfortunately we only scrape the year from IMDB! :'(
    * This is a first example of what happens when your algebras don't align.
    *
    * Sometimes by poor design, sometimes by physical reality. This time intentionally ;)
    */
  private def imdbMovieToMovieCreation(imdb: IMDBMovie): MovieCreation = MovieCreation(
    name = MovieTitle(IMDBTitle.exorcise(imdb.title)),
    date = imdb.year.map { y =>
      val year = ReleaseYear.exorcise(y)
      val ld   = LocalDate.of(year.getValue, 1, 1)
      ReleaseDate(ld)
    },
  )
}
