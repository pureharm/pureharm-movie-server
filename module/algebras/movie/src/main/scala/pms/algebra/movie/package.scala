package pms.algebra

import pms.core._
import java.time._

/**
  *
  * @author Lorand Szakacs, https://github.com/lorandszakacs
  * @since 25 Jun 2018
  *
  */
package object movie {
  object MovieID extends Sprout[Long]
  type MovieID = MovieID.Type

  object MovieTitle extends Sprout[String]
  type MovieTitle = MovieTitle.Type

  object ReleaseDate extends Sprout[LocalDate]
  type ReleaseDate = ReleaseDate.Type

  type QueryInterval = (ReleaseDate, ReleaseDate)
}
