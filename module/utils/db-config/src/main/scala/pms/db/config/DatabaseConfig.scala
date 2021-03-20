package pms.db.config

import pms._
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

final case class DBConnectionConfig(
  host:     String,
  port:     Int,
  dbName:   String,
  username: String,
  password: String,
  schema:   String,
) {
  def jdbcURL: String = s"jdbc:postgresql://$host:$port/$dbName?currentSchema=$schema"
}

final case class FlywayConfig(
  schemas:                 List[String] = List.empty,
  migrationLocations:      List[String] = List.empty,
  ignoreMissingMigrations: Boolean = false,
  cleanOnValidationError:  Boolean = false,
)

object DatabaseConfig {
  def resource[F[_]: Config]: Resource[F, DatabaseConfig] = ???
}
