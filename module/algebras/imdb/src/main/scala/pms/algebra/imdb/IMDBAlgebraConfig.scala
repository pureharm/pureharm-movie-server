package pms.algebra.imdb

import pms._
import pms.config._

import scala.concurrent.duration._

final case class IMDBAlgebraConfig(
  requestsInterval: FiniteDuration,
  requestsNumber:   Long,
)

object IMDBAlgebraConfig
