package pms.algebra.movie

/**
  *
  * See [[Movie]]
  * This class is used to specify all the data needed to create a movie
  *
  * @author Lorand Szakacs, https://github.com/lorandszakacs
  * @since 25 Jun 2018
  *
  *
  *        {
  *        "name": "Lord of The Rings: The Fellowship of the Ring",
  *        "date": "2002-02-01"
  *        }
  *
  */
final case class MovieCreation(
  name:          MovieTitle,
  date:          Option[ReleaseDate],
  coverImageURL: Option[CoverImageURL],
)
