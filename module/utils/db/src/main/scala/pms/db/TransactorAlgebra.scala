package pms.db

import busymachines.pureharm.db.DBConnectionConfig
import doobie.Transactor
import doobie.hikari.HikariTransactor
import pms.effects._
import scala.concurrent.ExecutionContext

object TransactorAlgebra {

  def resource[F[_]](
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
}
