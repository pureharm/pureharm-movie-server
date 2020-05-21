package pms.db.config

import pms.effects.Sync
import pms.config.ConfigLoader

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

  override def default[F[_]: Sync]: F[DatabaseConfig] =
    this.load[F]("pms.db")
}
