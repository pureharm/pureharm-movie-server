package pms.db.config

import pms.effects._
import pms.effects.implicits._
import doobie.util.transactor.Transactor
import busymachines.pureharm.db.flyway._

/**
  * @author Alexandru Stana, alexandru.stana@busymachines.com
  * @since 28/06/2018
  */
object DatabaseConfigAlgebra {

  def transactor[F[_]: Async: ContextShift](config: DatabaseConfig): F[Transactor[F]] = Async[F].delay {
    Transactor.fromDriverManager[F](
      driver = "org.postgresql.Driver",
      url    = config.connection.jdbcURL: String,
      user   = config.connection.username: String,
      pass   = config.connection.password: String,
    )
  }

  def initializeSQLDb[F[_]: Sync](config: DatabaseConfig): F[Int] = {
    val clean =
      if (config.forceClean)
        Flyway.clean(config.connection)
      else Sync[F].unit

    clean *> Flyway.migrate(config.connection, config.flyway)
  }
}
