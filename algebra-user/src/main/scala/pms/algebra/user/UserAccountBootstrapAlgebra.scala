package pms.algebra.user

/**
  *
  * @author Lorand Szakacs, https://github.com/lorandszakacs
  * @since 13 Jul 2018
  *
  */
final class UserAccountBootstrapAlgebra[F[_]] private (
  private val uca: UserAccountAlgebra[F]
) {

  def bootstrapUser(reg: UserRegistration): F[UserRegistrationToken] =
    uca.registrationStep1OP(reg)
}

object UserAccountBootstrapAlgebra {

  def impl[F[_]](uca: UserAccountAlgebra[F]): UserAccountBootstrapAlgebra[F] =
    new UserAccountBootstrapAlgebra[F](uca)
}
