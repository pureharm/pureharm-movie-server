package pms.db.config

import busymachines.pureharm.db.DBConnectionConfig
import busymachines.pureharm.db.flyway.FlywayConfig
import pms.config._
import pms.effects.Sync

/**
  * @author Alexandru Stana, alexandru.stana@busymachines.com
  * @since 28/06/2018
  */
final case class DatabaseConfig(
  driver:     String,
  connection: DBConnectionConfig,
  flyway:     Option[FlywayConfig],
  clean:      Boolean,
)

object DatabaseConfig extends ConfigLoader[DatabaseConfig] {
  implicit override def configReader: ConfigReader[DatabaseConfig] = semiauto.deriveReader[DatabaseConfig]
  override def default[F[_]: Sync]: F[DatabaseConfig] = this.load[F]("pms.db")
}
