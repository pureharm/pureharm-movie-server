package pms.server.config

import pms.config._
import pms._

final case class PMSPoolConfig(
  pgSqlPool:      Int,
  httpServerPool: Int,
)

object PMSPoolConfig {
  def resource[F[_]: Config]: Resource[F, PMSPoolConfig] = ???
}
