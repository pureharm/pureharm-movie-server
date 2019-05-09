package pms.algebra.movie

import busymachines.core._
import pms.core.AnomalyIDS

/**
  *
  * @author Lorand Szakacs, https://github.com/lorandszakacs
  * @since 09 May 2019
  *
  */

final case class MovieNotFoundAnomaly(mid: MovieID) extends NotFoundFailure(s"Movie with id '$mid' not found") {
  override val id: AnomalyID = AnomalyIDS.MovieNotFoundID

  override val parameters: Anomaly.Parameters = Anomaly.Parameters(
    "movieID" -> mid.toString,
  )
}
