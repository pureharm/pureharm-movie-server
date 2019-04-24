package pms.service.user

import cats.implicits._

import pms.core._
import pms.effects._
import pms.email._

import pms.algebra.user._

/**
  *
  * @author Lorand Szakacs, https://github.com/lorandszakacs
  * @since 26 Jun 2018
  *
  */
final class UserAccountService[F[_]] private (
  private val userAuth:     UserAuthAlgebra[F],
  private val userAccount:  UserAccountAlgebra[F],
  private val userAlgebra:  UserAlgebra[F],
  private val emailAlgebra: EmailAlgebra[F],
)(
  implicit private val F: Concurrent[F],
) {

  def registrationStep1(inv: UserInvitation)(implicit authCtx: AuthCtx): F[Unit] =
    for {
      regToken <- userAccount.registrationStep1(inv)
      _ <- forkAndForget {
        emailAlgebra.sendEmail(
          to = inv.email,
          //FIXME: resolve this data from an email content algebra or something
          subject = s"You have been invited to join Pure Movie Server as a :${inv.role.productPrefix}",
          //FIXME: resolve this data from an email content algebra or something
          content = s"Please click this link to finish registration: [link_to_frontend]/$regToken",
        ) //FIXME: do recoverWith and at least delete the user registration if sending email fails.
      }
    } yield ()

  def registrationStep2(token: UserRegistrationToken): F[User] =
    for {
      user <- userAccount.registrationStep2(token)
    } yield user

  def resetPasswordStep1(email: Email): F[Unit] =
    for {
      resetToken <- userAccount.resetPasswordStep1(email)
      _ <- forkAndForget {
        emailAlgebra.sendEmail(
          to      = email,
          subject = "Password reset for Pure Movie Server",
          content = s"Please click the following link to reset your account password: [link_to_FE]$resetToken",
        )
      }
    } yield ()

  def resetPasswordStep2(pwr: PasswordResetCompletion): F[Unit] =
    for {
      _ <- userAccount.resetPasswordStep2(pwr.token, pwr.newPws)
    } yield ()

  //TODO: create pretty syntax for this, since it will be used more often
  private def forkAndForget[A](f: F[A]): F[Unit] =
    F.start(f).void

}

object UserAccountService {

  def concurrent[F[_]: Concurrent](
    userAuth:     UserAuthAlgebra[F],
    userAccount:  UserAccountAlgebra[F],
    userAlgebra:  UserAlgebra[F],
    emailAlgebra: EmailAlgebra[F],
  ): UserAccountService[F] = new UserAccountService[F](
    userAuth,
    userAccount,
    userAlgebra,
    emailAlgebra,
  )
}
