package phms.db.config

/** @author Alexandru Stana, alexandru.stana@busymachines.com
  * @since 28/06/2018
  */
final case class DatabaseConfig(
  connection: DBConnectionConfig,
  flyway:     FlywayConfig,
)

final case class DBConnectionConfig(
  host:     DBHost,
  port:     DBPort,
  dbName:   DatabaseName,
  username: DBUsername,
  password: DBPassword,
  schema:   SchemaName,
) {
  def jdbcURL: JDBCUrl = JDBCUrl.postgresql(host, port, dbName, schema)
}

final case class FlywayConfig(
  schemas:                 List[SchemaName] = List.empty,
  migrationLocations:      List[MigrationLocation] = List.empty,
  ignoreMissingMigrations: IgnoreMissingMigrations = IgnoreMissingMigrations.False,
  cleanOnValidationError:  CleanOnValidationError = CleanOnValidationError.False,
)
