package phms.algebra.imdb

/** @author Lorand Szakacs, https://github.com/lorandszakacs
  * @since 25 Jun 2018
  */
final case class IMDBMovie(
  title: IMDBTitle,
  year:  Option[ReleaseYear],
)
