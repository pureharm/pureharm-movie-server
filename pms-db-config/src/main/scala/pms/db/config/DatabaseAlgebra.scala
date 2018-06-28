package pms.db.config

import cats.implicits._
import cats.effect.{Async, Sync}
import doobie.util.transactor.Transactor
import org.flywaydb.core.Flyway

/**
  * @author Alexandru Stana, alexandru.stana@busymachines.com
  * @since 28/06/2018
  */
object DatabaseAlgebra {

  def transactor[F[_]: Async](config: DatabaseConfig): F[Transactor[F]] = {
    val x: Transactor[F] = Transactor.fromDriverManager[F](config.driver, config.url, config.user, config.password)
    x.pure[F]
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
