package pms.algebra.imdb

import pms.effects._
import pms.config._

import scala.concurrent.duration._

final case class IMDBAlgebraConfig(
  requestsInterval: FiniteDuration,
  requestsNumber:   Long,
)

object IMDBAlgebraConfig extends ConfigLoader[IMDBAlgebraConfig] {
  import pms.effects.implicits._

  override def default[F[_]: Sync]: F[IMDBAlgebraConfig] =
    IMDBAlgebraConfigLoaderRepr.default.map { repr =>
      IMDBAlgebraConfig(FiniteDuration(repr.requestsInterval, MILLISECONDS), repr.requestsNumber)
    }

  override def configReader: ConfigReader[IMDBAlgebraConfig] = semiauto.deriveReader[IMDBAlgebraConfig]

  private case class IMDBAlgebraConfigRepr(
    requestsInterval: Long,
    requestsNumber:   Long,
  )

  private object IMDBAlgebraConfigLoaderRepr extends ConfigLoader[IMDBAlgebraConfigRepr] {

    implicit override def configReader: ConfigReader[IMDBAlgebraConfigRepr] =
      semiauto.deriveReader[IMDBAlgebraConfigRepr]

    override def default[F[_]: Sync]: F[IMDBAlgebraConfigRepr] =
      this.load[F]("algebra.imdb")
  }
}
