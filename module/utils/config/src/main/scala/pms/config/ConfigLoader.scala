package pms.config

import pms._
import ciris._

trait ConfigLoader[T] {
  def configValue: ConfigValue[Effect, T]
  def load[F[_]](implicit F:     Config[F]): F[T]           = F.load(configValue)
  def resource[F[_]](implicit F: Config[F]): Resource[F, T] = Resource.eval(F.load(configValue))
}
