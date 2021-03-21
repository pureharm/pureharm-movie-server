package pms.db

import pms.logger._
import pms._
import pms.db.config._

trait Flyway[F[_]] {
  def runMigrations(implicit logger: Logger[F]): F[Int]

  def cleanDB(implicit logger: Logger[F]): F[Unit]
}

object Flyway {

  def resource[F[_]: Sync](
    dbConfig: DBConnectionConfig
  ): Resource[F, Flyway[F]] =
    new FlywayImpl[F](connectionConfig = dbConfig, config = none).pure[Resource[F, *]].widen

  final private class FlywayImpl[F[_]: Sync](
    private[Flyway] val connectionConfig: DBConnectionConfig,
    private[Flyway] val config:           Option[FlywayConfig],
  ) extends Flyway[F] {

    override def runMigrations(implicit logger: Logger[F]): F[Int] =
      Flyway
        .migrate[F](dbConfig = connectionConfig, flywayConfig = config)
        .flatTap(migs => logger.info(s"Successfully applied: $migs flyway migrations"))

    override def cleanDB(implicit logger: Logger[F]): F[Unit] =
      for {
        _ <- logger.warn(s"CLEANING DB: ${connectionConfig.jdbcURL} — better make sure this isn't on prod, lol")
        _ <- Flyway.clean[F](dbConfig = connectionConfig)
      } yield ()
  }

  private object Flyway {
    import org.flywaydb.core.{Flyway => JFlyway}

    def migrate[F[_]](
      dbConfig:     DBConnectionConfig,
      flywayConfig: Option[FlywayConfig] = Option.empty,
    )(implicit F:   Sync[F]): F[Int] =
      for {
        fw   <- flywayInit[F](dbConfig.jdbcURL, dbConfig.username, dbConfig.password, flywayConfig)
        migs <- F.delay(fw.migrate())
      } yield migs.migrationsExecuted

    def clean[F[_]](
      dbConfig:   DBConnectionConfig
    )(implicit F: Sync[F]): F[Unit] =
      this.clean[F](url = dbConfig.jdbcURL, username = dbConfig.username, password = dbConfig.password)

    def clean[F[_]](
      url:        JDBCUrl,
      username:   DBUsername,
      password:   DBPassword,
    )(implicit F: Sync[F]): F[Unit] =
      for {
        fw <- flywayInit[F](url, username, password, Option.empty)
        _  <- F.delay(fw.clean())
      } yield ()

    private def flywayInit[F[_]](
      url:        JDBCUrl,
      username:   DBUsername,
      password:   DBPassword,
      config:     Option[FlywayConfig],
    )(implicit F: Sync[F]): F[JFlyway] =
      F.delay {
        val fwConfig = JFlyway.configure()
        fwConfig.dataSource(url, username, password)
        fwConfig.mixed(true)
        config match {
          case None    => () //default everything. Do nothing, lol, java
          case Some(c) =>
            if (c.migrationLocations.nonEmpty) {
              fwConfig.locations(c.migrationLocations.map(MigrationLocation.oldType): _*)
            }
            if (c.schemas.nonEmpty) {
              fwConfig.schemas(c.schemas: _*)
            }
            fwConfig.ignoreMissingMigrations(c.ignoreMissingMigrations)
            fwConfig.cleanOnValidationError(c.cleanOnValidationError)
        }

        new JFlyway(fwConfig)
      }
  }
}
