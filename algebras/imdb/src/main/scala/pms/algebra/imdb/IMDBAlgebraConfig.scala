package pms.algebra.imdb

import pms.effects._
import pms.config.ConfigLoader

import scala.concurrent.duration._

final case class IMDBAlgebraConfig(
    requestsInterval: FiniteDuration,
    requestsNumber: Long,
)

object IMDBAlgebraConfig extends ConfigLoader[IMDBAlgebraConfig] {
  import pms.effects.implicits._
  override def default[F[_]: Sync]: F[IMDBAlgebraConfig] =
    IMDBAlgebraConfigLoaderRepr.default.map { repr =>
      IMDBAlgebraConfig(FiniteDuration(repr.requestsInterval, MILLISECONDS),
                        repr.requestsNumber)
    }

  private case class IMDBAlgebraConfigRepr(
      requestsInterval: Long,
      requestsNumber: Long,
  )

  private object IMDBAlgebraConfigLoaderRepr
      extends ConfigLoader[IMDBAlgebraConfigRepr] {
    override def default[F[_]: Sync]: F[IMDBAlgebraConfigRepr] =
      this.load[F]("algebra.imdb")
  }
}
