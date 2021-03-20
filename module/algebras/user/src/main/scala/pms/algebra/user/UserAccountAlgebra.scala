package pms.algebra.user

import pms.algebra.user.impl.UserAlgebraImpl
import pms._
import pms.db._

/**
  *
  * @author Lorand Szakacs, https://github.com/lorandszakacs
  * @since 20 Jun 2018
  *
  */
trait UserAccountAlgebra[F[_]] {

  implicit protected def monadError: MonadError[F, Throwable]
  protected def authAlgebra:         UserAuthAlgebra[F]

  final def invitationStep1(
    inv:  UserInvitation
  )(implicit
    auth: AuthCtx
  ): F[UserInviteToken] =
    authAlgebra.authorizeGTERoleThan(inv.role)(registrationStep1Impl(inv))

  protected[user] def registrationStep1Impl(inv: UserInvitation): F[UserInviteToken]

  def invitationStep2(token: UserInviteToken, pw: PlainTextPassword): F[User]

  def resetPasswordStep1(email: Email): F[PasswordResetToken]

  def resetPasswordStep2(token: PasswordResetToken, newPassword: PlainTextPassword): F[Unit]
}

object UserAccountAlgebra {

  def resource[F[_]](implicit transactor: Transactor[F], F: Async[F]): Resource[F, UserAccountAlgebra[F]] =
    Resource.pure[F, UserAccountAlgebra[F]](new UserAlgebraImpl[F]())
}
