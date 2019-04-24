package pms.db.config

import cats.effect.{Async, ContextShift, Sync}
import doobie.util.transactor.Transactor
import org.flywaydb.core.Flyway

/**
  * @author Alexandru Stana, alexandru.stana@busymachines.com
  * @since 28/06/2018
  */
object DatabaseConfigAlgebra {

  def transactor[F[_]: Async: ContextShift](config: DatabaseConfig): F[Transactor[F]] = Async[F].delay {
    Transactor.fromDriverManager[F](config.driver, config.url, config.user, config.password)
  }

  /**
    * Necessary to remove log warning:
    * {{{
    *   Could not find schema history table "public"."flyway_schema_history",
    *   but found "public"."schema_version" instead.
    *   You are seeing this message because Flyway changed its default for flyway.table
    *   in version 5.0.0 to flyway_schema_history and you are still relying on the old
    *   default (schema_version). Set flyway.table=schema_version in your configuration to fix this.
    *   This fallback mechanism will be removed in Flyway 6.0.0.
    * }}}
    */
  private val FlywayMigrationHistory = "schema_version"

  def initializeSQLDb[F[_]: Sync](config: DatabaseConfig): F[Int] =
    Sync[F].delay {
      val fwConfig = Flyway.configure()
      fwConfig.dataSource(config.url, config.user, config.password)
      fwConfig.table(FlywayMigrationHistory)
      val fw = new Flyway(fwConfig)
      if (config.clean) fw.clean()
      fw.migrate()
    }
}
