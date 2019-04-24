package pms.algebra.user

import cats.Applicative

/**
  *
  * @author Lorand Szakacs, https://github.com/lorandszakacs
  * @since 13 Jul 2018
  *
  */
final class UserAccountBootstrapAlgebra[F[_]] private (
  private val uca: UserAccountAlgebra[F],
) {

  def bootstrapUser(reg: UserRegistration): F[UserRegistrationToken] =
    uca.registrationStep1Impl(reg)
}

object UserAccountBootstrapAlgebra {

  def impl[F[_]: Applicative](uca: UserAccountAlgebra[F]): F[UserAccountBootstrapAlgebra[F]] =
    Applicative[F].pure(new UserAccountBootstrapAlgebra[F](uca))
}
