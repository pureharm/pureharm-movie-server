package pms.algebra.user

import doobie.util.transactor.Transactor
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
    reg: UserRegistration
  )(
    implicit auth: AuthCtx
  ): F[UserRegistrationToken] = authAlgebra.authorizeGTERole(reg.role)(registrationStep1OP(reg))

  protected[user] def registrationStep1OP(reg: UserRegistration): F[UserRegistrationToken]

  def registrationStep2(token: UserRegistrationToken): F[User]

  def resetPasswordStep1(email: Email): F[PasswordResetToken]

  def resetPasswordStep2(token: PasswordResetToken, newPassword: PlainTextPassword): F[Unit]
}

object UserAccountAlgebra {

  def async[F[_]: Async](implicit transactor: Transactor[F]): UserAccountAlgebra[F] =
    new impl.AsyncAlgebraImpl[F]()
}
