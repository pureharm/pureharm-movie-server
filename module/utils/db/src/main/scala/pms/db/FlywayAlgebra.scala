package pms.db

import busymachines.pureharm.db.DBConnectionConfig
import busymachines.pureharm.db.flyway._
import io.chrisdavenport.log4cats.Logger
import pms.core._
import pms.effects._

trait FlywayAlgebra[F[_]] {
  def runMigrations(implicit logger: Logger[F]): F[Int]

  def cleanDB(implicit logger: Logger[F]): F[Unit]
}

object FlywayAlgebra {

  def resource[F[_]: Sync](
    dbConfig: DBConnectionConfig
  ): Resource[F, FlywayAlgebra[F]] =
    new FlywayAlgebraImpl[F](connectionConfig = dbConfig, config = none).pure[Resource[F, *]]

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
}
