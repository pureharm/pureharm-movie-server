package pms.algebra.movie

/**
  *
  * See [[Movie]]
  * This class is used to specify all the data needed to create a movie
  *
  * @author Lorand Szakacs, https://github.com/lorandszakacs
  * @since 25 Jun 2018
  *
  */
final case class MovieCreation(
    name: MovieTitle,
    date: Option[ReleaseDate],
)
