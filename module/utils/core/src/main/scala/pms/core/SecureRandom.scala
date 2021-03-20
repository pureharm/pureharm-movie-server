package pms.core

sealed trait SecureRandom[F[_]] extends Random[F]

object SecureRandom {

  def resource[F[_]: Sync]: Resource[F, SecureRandom[F]] =
    Resource.eval[F, SecureRandom[F]] {
      Random.javaSecuritySecureRandom(8).map { r: Random[F] =>
        new SecureRandom[F] {
          override def betweenDouble(minInclusive: Double, maxExclusive: Double): F[Double] =
            r.betweenDouble(minInclusive, maxExclusive)
          override def betweenFloat(minInclusive:  Float, maxExclusive:  Float):  F[Float]  =
            r.betweenFloat(minInclusive, maxExclusive)
          override def betweenInt(minInclusive:    Int, maxExclusive:    Int):    F[Int]    =
            r.betweenInt(minInclusive, maxExclusive)
          override def betweenLong(minInclusive:   Long, maxExclusive:   Long):   F[Long]   =
            r.betweenLong(minInclusive, maxExclusive)
          override def nextAlphaNumeric: F[Char]    = r.nextAlphaNumeric
          override def nextBoolean:      F[Boolean] = r.nextBoolean
          override def nextBytes(n: Int): F[Array[Byte]] = r.nextBytes(n)
          override def nextDouble:   F[Double] = r.nextDouble
          override def nextFloat:    F[Float]  = r.nextFloat
          override def nextGaussian: F[Double] = r.nextGaussian
          override def nextInt:      F[Int]    = r.nextInt
          override def nextIntBounded(n: Int): F[Int] = r.nextIntBounded(n)
          override def nextLong: F[Long] = r.nextLong
          override def nextLongBounded(n: Long): F[Long] = r.nextLongBounded(n)
          override def nextPrintableChar: F[Char] = r.nextPrintableChar
          override def nextString(length:  Int):       F[String]    = r.nextString(length)
          override def shuffleList[A](l:   List[A]):   F[List[A]]   = r.shuffleList(l)
          override def shuffleVector[A](v: Vector[A]): F[Vector[A]] = r.shuffleVector(v)
        }

      }
    }
}
