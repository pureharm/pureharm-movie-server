package phms

sealed trait SecureRandom[F[_]] {
  def sync: Sync[F]
  def nextBytes(n:         Int): F[Array[Byte]]
  def nextBytesAsBase64(n: Int): F[String]
}

object SecureRandom {

  def resource[F[_]](implicit F: Sync[F]): Resource[F, SecureRandom[F]] =
    Resource.eval[F, SecureRandom[F]] {
      cats.effect.std.Random.javaSecuritySecureRandom(8).map { r: Random[F] =>
        new SecureRandom[F] {
          override def sync: Sync[F] = F
          override def nextBytes(n:         Int): F[Array[Byte]] = r.nextBytes(n)
          override def nextBytesAsBase64(n: Int): F[String]      =
            Stream
              .evalSeq(this.nextBytes(n).map(_.toSeq))
              .through(fs2.text.base64.encode[F])
              .compile
              .string
        }
      }
    }
}
