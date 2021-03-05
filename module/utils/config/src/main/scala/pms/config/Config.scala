package pms.config

import pms.core._
/**
  * Capability for reading config files.
  *
  * Used to signal that something in our app
  * reads configurations
  */
trait Config[F[_]] {}

object Config {
  def resource[F[_]: Sync]: Resource[F, Config[F]] = new Config[F] {}.pure[Resource[F, *]]
}
