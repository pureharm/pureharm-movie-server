package pms.algebra.imdb

import pms.core._
import pms.config._

import scala.concurrent.duration._

final case class IMDBAlgebraConfig(
  requestsInterval: FiniteDuration,
  requestsNumber:   Long,
)

object IMDBAlgebraConfig {
  def resource[F[_]: Config]: Resource[F, IMDBAlgebraConfig] = ???
}
