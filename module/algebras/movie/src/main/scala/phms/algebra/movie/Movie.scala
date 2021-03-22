package phms.algebra.movie

/** @author Lorand Szakacs, https://github.com/lorandszakacs
  * @since 25 Jun 2018
  */
//TODO: add as many relevant details as can be reasonably scraped from IMDB.
// see phms.service.movie.IMDBService.scrapeIMDBForTitle
final case class Movie(
  id:   MovieID,
  name: MovieTitle,
  date: Option[ReleaseDate] = None,
)
