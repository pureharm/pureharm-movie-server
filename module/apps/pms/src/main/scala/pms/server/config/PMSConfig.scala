package pms.server.config

import pms.config._
import pms._

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

object PMSConfig {
  def resource[F[_]: Config]: Resource[F, PMSConfig] = ???
}
