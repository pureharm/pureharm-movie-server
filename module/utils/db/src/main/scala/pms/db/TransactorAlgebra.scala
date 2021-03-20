package pms.db

import pms._
import pms.db.config.DBConnectionConfig

import scala.concurrent.ExecutionContext

object TransactorAlgebra {

  def resource[F[_]](
    config:      DBConnectionConfig
  )(implicit as: Async[F]): Resource[F, Transactor[F]] =
    Fail.nicata("Transactor").raiseError[Resource[F, *], Transactor[F]]
}
