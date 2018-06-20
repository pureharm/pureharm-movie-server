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

  implicit def monadError:   MonadError[F, Throwable]
  protected def authAlgebra: UserAuthAlgebra[F]

  final def registrationStep1(
    email: Email,
    pw:    PlainTextPassword,
    role:  UserRole
  )(
    implicit auth: AuthCtx
  ): F[UserRegistrationToken] = authAlgebra.authorizeGTERole(role)(registrationStep1OP(email, pw, role))

  protected def registrationStep1OP(email: Email, pw: PlainTextPassword, role: UserRole): F[UserRegistrationToken]

  def registrationStep2(token: UserRegistrationToken): F[User]

  def resetPasswordStep1(email: Email): F[PasswordResetToken]

  def resetPasswordStep2(token: PasswordResetToken, newPassword: PlainTextPassword): F[Unit]
}

object UserAccountAlgebra {
  import pms.effects._

  def async[F[_]: Async]: UserAccountAlgebra[F] = new impl.AsyncAlgebraImpl[F]()
}
