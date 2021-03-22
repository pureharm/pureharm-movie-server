package phms.algebra.imdb

import phms._
import phms.config._

import scala.concurrent.duration._

final case class IMDBAlgebraConfig(
  requestsInterval: FiniteDuration,
  requestsNumber:   Long,
)

object IMDBAlgebraConfig
