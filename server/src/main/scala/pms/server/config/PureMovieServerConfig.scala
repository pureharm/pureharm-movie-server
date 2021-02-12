package pms.server.config

import pms.config._
import pms.effects._

/**
  *
  * @author Lorand Szakacs, https://github.com/lorandszakacs
  * @since 20 Jun 2018
  *
  */
final case class PureMovieServerConfig(
  port:      Int,
  host:      String,
  apiRoot:   String,
  bootstrap: Boolean,
)

object PureMovieServerConfig extends ConfigLoader[PureMovieServerConfig] {

  override implicit def configReader: ConfigReader[PureMovieServerConfig] =
    semiauto.deriveReader[PureMovieServerConfig]

  override def default[F[_]: Sync]: F[PureMovieServerConfig] =
    this.load[F]("pms.server")
}
