package pms.db

import pms.core._
import pms.db.config.DBConnectionConfig

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
        url             = config.jdbcURL,
        user            = config.username,
        pass            = config.password,
        connectEC       = connectionExecutionContext,
        blocker         = blocker,
      )
    } yield xa
  }
}
