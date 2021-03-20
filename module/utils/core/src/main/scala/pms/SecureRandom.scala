package pms

sealed trait SecureRandom[F[_]] {
  def nextBytes(n: Int): F[Array[Byte]]
}

object SecureRandom {

  def resource[F[_]: Sync]: Resource[F, SecureRandom[F]] =
    Resource.eval[F, SecureRandom[F]] {
      cats.effect.std.Random.javaSecuritySecureRandom(8).map { r: Random[F] =>
        new SecureRandom[F] {
          override def nextBytes(n: Int): F[Array[Byte]] = r.nextBytes(n)
        }
      }
    }
}
