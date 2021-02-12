package pms.db.config

import busymachines.pureharm.db.DBConnectionConfig
import busymachines.pureharm.db.flyway.Flyway
import doobie.hikari.HikariTransactor
import doobie.util.transactor.Transactor
import pms.effects._
import pms.effects.implicits._
import scala.concurrent.ExecutionContext

object DatabaseConfigAlgebra {

  def transactor[F[_]](
    connectionExecutionContext: ExecutionContext,
    config:                     DBConnectionConfig,
  )(implicit as:                Async[F], cs: ContextShift[F]): Resource[F, Transactor[F]] =
    for {
      blocker <- Blocker(as)
      xa      <- HikariTransactor.newHikariTransactor(
        driverClassName = "org.postgresql.Driver",
        url             = config.jdbcURL: String,
        user            = config.username: String,
        pass            = config.password: String,
        connectEC       = connectionExecutionContext,
        blocker         = blocker,
      )
    } yield xa

  def applyMigrations[F[_]: Sync](config: DatabaseConfig): F[Int] = {
    val clean =
      if (config.clean) Flyway.clean(config.connection)
      else Sync[F].unit

    clean *> Flyway.migrate(config.connection, config.flyway)
  }

}
