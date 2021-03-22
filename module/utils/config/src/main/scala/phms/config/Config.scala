package phms.config

import phms._
/** Capability for reading config files.
  *
  * Used to signal that something in our app
  * reads configurations
  */
import ciris._

sealed trait Config[F[_]] {
  implicit protected[this] val async: Async[F]
  def load[T](value: ConfigValue[F, T]): F[T] = value.load[F]
}

object Config {

  def resource[F[_]](implicit F: Async[F]): Resource[F, Config[F]] =
    new Config[F] {
      override protected[this] val async: Async[F] = F
    }.pure[Resource[F, *]]
}
