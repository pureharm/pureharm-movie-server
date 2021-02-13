package pms.algebra.user

import pms.core._
import pms.effects.Applicative

/**
  *
  * @author Lorand Szakacs, https://github.com/lorandszakacs
  * @since 13 Jul 2018
  *
  */
final class UserAccountBootstrapAlgebra[F[_]] private (
  private val uca: UserAccountAlgebra[F]
) {

  def bootstrapUser(inv: UserInvitation): F[UserInviteToken] =
    uca.registrationStep1Impl(inv)
}

object UserAccountBootstrapAlgebra {

  def resource[F[_]: Applicative](uca: UserAccountAlgebra[F]): Resource[F, UserAccountBootstrapAlgebra[F]] =
    Resource.pure(new UserAccountBootstrapAlgebra[F](uca))
}
