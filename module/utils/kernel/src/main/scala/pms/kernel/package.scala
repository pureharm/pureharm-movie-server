package pms

package object kernel {

  type Email = Email.Type

  object Email extends SproutRefinedThrow[String] with SproutEq[String] with SproutOrder[String] {

    override def refine[F[_]](o: String)(implicit F: MonadThrow[F]): F[String] =
      if (o.contains("@")) Fail.invalid("Email must contain: @").raiseError[F, String] else o.pure[F]
  }

  type PlainTextPassword = PlainTextPassword.Type

  object PlainTextPassword extends SproutRefinedThrow[String] {

    /** TODO: make these rules configurable
      */
    override def refine[F[_]](o: String)(implicit F: MonadThrow[F]): F[String] =
      if (o.length < 6) Fail.invalid("Password needs to have at least 6 characters").raiseError[F, String]
      else o.pure[F]
  }
}
