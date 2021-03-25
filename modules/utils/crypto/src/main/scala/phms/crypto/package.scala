package phms

package object crypto {

  object BCryptHash extends SproutRefinedThrow[Array[Byte]] {

    override def refine[F[_]](o: Array[Byte])(implicit m: MonadThrow[F]): F[Array[Byte]] =
      if (Option(o).isEmpty)
        Fail.invalid("Invalid BCryptHash, cannot be null").raiseError[F, Array[Byte]]
      else if (o.isEmpty)
        Fail.invalid("Invalid BCryptHash, cannot be an empty array").raiseError[F, Array[Byte]]
      else
        o.pure[F]
  }
  type BCryptHash = BCryptHash.Type
}
