package pms.db

import pms.logger._
import pms._
import pms.db.config._

trait FlywayAlgebra[F[_]] {
  def runMigrations(implicit logger: Logger[F]): F[Int]

  def cleanDB(implicit logger: Logger[F]): F[Unit]
}

object FlywayAlgebra {

  def resource[F[_]: Sync](
    dbConfig: DBConnectionConfig
  ): Resource[F, FlywayAlgebra[F]] =
    new FlywayAlgebraImpl[F](connectionConfig = dbConfig, config = none).pure[Resource[F, *]].widen

  final private class FlywayAlgebraImpl[F[_]: Sync](
    private[FlywayAlgebra] val connectionConfig: DBConnectionConfig,
    private[FlywayAlgebra] val config:           Option[FlywayConfig],
  ) extends FlywayAlgebra[F] {

    override def runMigrations(implicit logger: Logger[F]): F[Int] = {
      val mig = Flyway.migrate[F](dbConfig = connectionConfig, flywayConfig = config)
      mig.flatTap(migs => logger.info(s"Successfully applied: $migs flyway migrations"))
    }

    override def cleanDB(implicit logger: Logger[F]): F[Unit] = {
      val mig = Flyway.clean[F](dbConfig = connectionConfig)
      logger.warn(s"CLEANING DB: ${connectionConfig.jdbcURL} — better make sure this isn't on prod, lol") >> mig
    }
  }

  private object Flyway {
    import org.flywaydb.core.{Flyway => JFlyway}

    def migrate[F[_]](
      url:          String,
      username:     String,
      password:     String,
      flywayConfig: Option[FlywayConfig],
    )(implicit
      F:            Sync[F]
    ): F[Int] =
      for {
        fw   <- flywayInit[F](url, username, password, flywayConfig)
        migs <- F.delay(fw.migrate())
      } yield migs.migrationsExecuted

    def migrate[F[_]](
      dbConfig:     DBConnectionConfig,
      flywayConfig: Option[FlywayConfig] = Option.empty,
    )(implicit
      F:            Sync[F]
    ): F[Int] =
      for {
        fw   <- flywayInit[F](dbConfig.jdbcURL, dbConfig.username, dbConfig.password, flywayConfig)
        migs <- F.delay(fw.migrate())
      } yield migs.migrationsExecuted

    def clean[F[_]: Sync](dbConfig: DBConnectionConfig): F[Unit] =
      this.clean[F](url = dbConfig.jdbcURL, username = dbConfig.username, password = dbConfig.password)

    def clean[F[_]: Sync](url:      String, username: String, password: String): F[Unit] =
      for {
        fw <- flywayInit[F](url, username, password, Option.empty)
        _  <- Sync[F].delay(fw.clean())
      } yield ()

    private def flywayInit[F[_]](
      url:        String,
      username:   String,
      password:   String,
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
              fwConfig.locations(c.migrationLocations: _*)
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
