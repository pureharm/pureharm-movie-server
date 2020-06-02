package pms.algebra

import pms.core._
import java.time._
import spire.math._

/**
  *
  * @author Lorand Szakacs, https://github.com/lorandszakacs
  * @since 25 Jun 2018
  *
  */
package object movie {
  object MovieID extends PhantomType[Long]
  type MovieID = MovieID.Type

  object MovieTitle extends PhantomType[String]
  type MovieTitle = MovieTitle.Type

  object ReleaseDate extends PhantomType[LocalDate]
  type ReleaseDate = ReleaseDate.Type

  implicit val releaseDateOrder: spire.algebra.Order[ReleaseDate] =
    new spire.algebra.Order[ReleaseDate] {

      override def compare(x: ReleaseDate, y: ReleaseDate): Int = {
        val dateX = ReleaseDate.despook(x)
        val dateY = ReleaseDate.despook(y)
        dateX.compareTo(dateY)
      }
    }

  //TODO: spire.math.Interval is too much of an overkill for a
  // range expressible for a an http request query.
  // Consider alternatives that are less powerful in what they do.
  // Spire is very good for expressing math, not the best for what's
  // a good idea in a DB query. For instance, the specific subtype
  // of spire.math.Bounded would be a better fit, but it's private
  // to spire.
  type QueryInterval = Interval[ReleaseDate]
}
