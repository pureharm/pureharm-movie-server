/*
 * Copyright 2021 BusyMachines
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package phms.db

import phms.logger._
import phms._
import phms.db.config._

trait Flyway[F[_]] {
  def runMigrations(implicit logger: Logger[F]): F[Int]

  def cleanDB(implicit logger: Logger[F]): F[Unit]
}

object Flyway {

  def resource[F[_]: Sync](
    dbConfig:     DBConnectionConfig,
    flywayConfig: FlywayConfig,
  ): Resource[F, Flyway[F]] =
    new FlywayImpl[F](connectionConfig = dbConfig, config = flywayConfig).pure[Resource[F, *]].widen

  final private class FlywayImpl[F[_]: Sync](
    private[Flyway] val connectionConfig: DBConnectionConfig,
    private[Flyway] val config:           FlywayConfig,
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
      flywayConfig: FlywayConfig,
    )(implicit F:   Sync[F]): F[Int] =
      for {
        fw   <- flywayInit[F](dbConfig.jdbcURL, dbConfig.username, dbConfig.password, Option(flywayConfig))
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
