package pms.db

import busymachines.pureharm.db.DBConnectionConfig

import pms.core._
import scala.concurrent.ExecutionContext

object TransactorAlgebra {

  def resource[F[_]](
    connectionExecutionContext: ExecutionContext,
    config:                     DBConnectionConfig,
  )(implicit as:                Async[F], cs: ContextShift[F]): Resource[F, Transactor[F]] = {
    import doobie.hikari.HikariTransactor
    for {
      blocker <- Blocker(as)
      xa      <- HikariTransactor.newHikariTransactor[F](
        driverClassName = "org.postgresql.Driver",
        url             = config.jdbcURL: String,
        user            = config.username: String,
        pass            = config.password: String,
        connectEC       = connectionExecutionContext,
        blocker         = blocker,
      )
    } yield xa
  }
}
