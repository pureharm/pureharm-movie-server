package phms.algebra.imdb

import scala.concurrent.duration._

final case class IMDBAlgebraConfig(
  requestsInterval: FiniteDuration,
  requestsNumber:   Long,
)
