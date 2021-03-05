package pms.server.config

import pms.config._
import pms.core._

/**
  *
  * @author Lorand Szakacs, https://github.com/lorandszakacs
  * @since 20 Jun 2018
  *
  */
final case class PMSConfig(
  port:      Int,
  host:      String,
  apiRoot:   String,
  bootstrap: Boolean,
)

object PMSConfig extends ConfigLoader[PMSConfig] {

  override implicit def configReader: ConfigReader[PMSConfig] =
    semiauto.deriveReader[PMSConfig]

  override def default[F[_]: Sync]: F[PMSConfig] =
    this.load[F]("pms.server")
}
