package pms.db.config

import busymachines.pureharm.db.DBConnectionConfig
import busymachines.pureharm.db.flyway.FlywayConfig
import pms.config._

/**
  * @author Alexandru Stana, alexandru.stana@busymachines.com
  * @since 28/06/2018
  */
final case class DatabaseConfig(
  connection: DBConnectionConfig,
  flyway:     Option[FlywayConfig],
  forceClean: Boolean,
)

object DatabaseConfig extends ConfigLoader[DatabaseConfig] {
  implicit override def configReader: ConfigReader[DatabaseConfig] = semiauto.deriveReader[DatabaseConfig]
}
