package pms.db.config

import cats.effect.{Async, Sync}
import doobie.util.transactor.Transactor
import org.flywaydb.core.Flyway

/**
  * @author Alexandru Stana, alexandru.stana@busymachines.com
  * @since 28/06/2018
  */
object DatabaseConfigAlgebra {

  def transactor[F[_]: Async](config: DatabaseConfig): F[Transactor[F]] = Async[F].delay {
    Transactor.fromDriverManager[F](config.driver, config.url, config.user, config.password)
  }

  def initializeSQLDb[F[_]](config: DatabaseConfig)(implicit S: Sync[F]): F[Unit] =
    S.delay {
      val fw = new Flyway()
      fw.setDataSource(config.url, config.user, config.password)
      if (config.clean) fw.clean()
      fw.migrate()
      ()
    }
}
