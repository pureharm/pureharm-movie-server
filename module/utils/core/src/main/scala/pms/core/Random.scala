package pms.core

/** Pseudo companion for [[cats.effect.std.Random]] that
  * exposes the one method we use to create randomness in our app
  */
object Random {

  def resource[F[_]: Sync]: Resource[F, Random[F]] =
    cats.effect.std.Random.javaUtilConcurrentThreadLocalRandom[F].pure[Resource[F, *]]
}
