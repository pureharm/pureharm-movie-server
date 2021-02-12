package pms.db

import doobie.hikari.HikariTransactor
import doobie.util.transactor.Transactor
import pms.effects.{Async, Blocker, ContextShift, Resource}
import scala.concurrent.ExecutionContext

trait Transactor {
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
}
