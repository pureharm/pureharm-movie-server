package pms.algebra.imdb

/**
  *
  * @author Lorand Szakacs, https://github.com/lorandszakacs
  * @since 25 Jun 2018
  *
  */
trait IMDBAlgebra[F[_]] {
  def scrapeMovieByTitle(title: TitleQuery): F[Option[IMDBMovie]]
}
