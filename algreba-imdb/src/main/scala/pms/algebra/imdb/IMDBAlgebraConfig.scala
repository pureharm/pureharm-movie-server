package pms.algebra.imdb

import cats.effect.Sync
import pms.config.ConfigLoader

final case class IMDBAlgebraConfig(
  requestsInterval: Long,
  requestsNumber:   Long
)

object IMDBAlgebraConfig extends ConfigLoader[IMDBAlgebraConfig] {
  override def default[F[_]: Sync]: F[IMDBAlgebraConfig] =
    this.load[F]("algebra.imdb")
}
