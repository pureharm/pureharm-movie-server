package pms.algebra.user

import pms.effects._
import pms.core._

/**
  *
  * @author Lorand Szakacs, https://github.com/lorandszakacs
  * @since 20 Jun 2018
  *
  */
trait UserAccountAlgebra[F[_]] {

  implicit protected def monadError: MonadError[F, Throwable]
  protected def authAlgebra:         UserAuthAlgebra[F]

  final def registrationStep1(
    inv: UserInvitation,
  )(
    implicit auth: AuthCtx,
  ): F[UserRegistrationToken] = authAlgebra.authorizeGTERole(inv.role)(registrationStep1Impl(inv))

  protected[user] def registrationStep1Impl(inv: UserInvitation): F[UserRegistrationToken]

  def registrationStep2(token: UserRegistrationToken): F[User]

  def resetPasswordStep1(email: Email): F[PasswordResetToken]

  def resetPasswordStep2(token: PasswordResetToken, newPassword: PlainTextPassword): F[Unit]
}
