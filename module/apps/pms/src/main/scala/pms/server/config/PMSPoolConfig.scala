package pms.server.config

import pms.config._
import pms.core._

final case class PMSPoolConfig(
  pgSqlPool:      Int,
  httpServerPool: Int,
)

object PMSPoolConfig extends ConfigLoader[PMSPoolConfig] {

  implicit override def configReader: ConfigReader[PMSPoolConfig] = semiauto.deriveReader[PMSPoolConfig]

  override def default[F[_]: Sync]: F[PMSPoolConfig] = this.load[F]("pms.pools")
}
