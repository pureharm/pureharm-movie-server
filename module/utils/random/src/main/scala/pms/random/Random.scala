package pms.random

import pms.core._

trait Random[F[_]] {}

object Random {

  def resource[F[_]: Sync]: Resource[F, Random[F]] = {
    val r = new Random[F] {}

    r.pure[Resource[F, *]]
  }
}
