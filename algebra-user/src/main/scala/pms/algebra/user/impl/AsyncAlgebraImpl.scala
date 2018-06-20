package pms.algebra.user.impl

import pms.core._
import pms.effects._
import pms.algebra.user._

/**
  *
  * @author Lorand Szakacs, https://github.com/lorandszakacs
  * @since 21 Jun 2018
  *
  */
final private[user] class AsyncAlgebraImpl[F[_]](
  implicit
  val F:                   Async[F],
  override val monadError: MonadError[F, Throwable]
) extends UserAuthAlgebra()(monadError) with UserAccountAlgebra[F] with UserAlgebra[F] {

  override protected def authAlgebra: UserAuthAlgebra[F] = this

  override def authenticate(email: Email, pw: PlainTextPassword): F[AuthCtx] =
    F.raiseError(new NotImplementedError("Cannot authenticate w/ email and password yet"))

  override def authenticate(token: AuthenticationToken): F[AuthCtx] =
    F.raiseError(new NotImplementedError("Cannot authenticate with token yet"))

  override protected[user] def promoteUserOP(id: UserID, newRole: UserRole): F[Unit] =
    F.raiseError(new NotImplementedError("Cannot promote user yet"))

  override protected def registrationStep1OP(
    email: Email,
    pw:    PlainTextPassword,
    role:  UserRole
  ): F[UserRegistrationToken] =
    F.raiseError(new NotImplementedError("Cannot perform registration step 1 OP at this time"))

  override def registrationStep2(token: UserRegistrationToken): F[User] =
    F.raiseError(new NotImplementedError("Cannot perform registration step 2 at this time"))

  override def resetPasswordStep1(email: Email): F[PasswordResetToken] =
    F.raiseError(new NotImplementedError("Cannot perform resetPassword step 1 at this time"))

  override def resetPasswordStep2(token: PasswordResetToken, newPassword: PlainTextPassword): F[Unit] =
    F.raiseError(new NotImplementedError("Cannot perform resetPassword step 2 at this time"))

  override def findUser(id: UserID)(implicit auth: AuthCtx): F[Option[User]] =
    F.raiseError(new NotImplementedError("Cannot perform find user operation at this time"))
}
