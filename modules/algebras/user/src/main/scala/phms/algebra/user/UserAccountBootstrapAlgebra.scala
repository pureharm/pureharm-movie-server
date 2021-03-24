package phms.algebra.user

import phms._

/** @author Lorand Szakacs, https://github.com/lorandszakacs
  * @since 13 Jul 2018
  */
final class UserAccountBootstrapAlgebra[F[_]] private (
  private val uca: UserAccountAlgebra[F]
) {

  def bootstrapUser(inv: UserInvitation): F[UserInviteToken] =
    uca.registrationStep1Impl(inv)
}

object UserAccountBootstrapAlgebra {

  def resource[F[_]](uca: UserAccountAlgebra[F])(implicit
    F:                    MonadThrow[F]
  ): Resource[F, UserAccountBootstrapAlgebra[F]] =
    new UserAccountBootstrapAlgebra[F](uca).pure[Resource[F, *]].widen
}
