package pms.db.config

import pms.config._
import pms.effects.Sync

/**
  * @author Alexandru Stana, alexandru.stana@busymachines.com
  * @since 28/06/2018
  */
final case class DatabaseConfig(
  driver:   String,
  url:      String,
  user:     String,
  password: String,
  clean:    Boolean,
)

object DatabaseConfig extends ConfigLoader[DatabaseConfig] {
  implicit override def configReader: ConfigReader[DatabaseConfig] = semiauto.deriveReader[DatabaseConfig]
  override def default[F[_]: Sync]: F[DatabaseConfig] = this.load[F]("pms.db")
}
