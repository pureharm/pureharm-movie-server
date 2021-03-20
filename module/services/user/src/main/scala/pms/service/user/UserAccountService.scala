package pms.service.user

import pms._
import pms.kernel._
import pms.email._
import pms.algebra.user._

/** @author Lorand Szakacs, https://github.com/lorandszakacs
  * @since 26 Jun 2018
  */
final class UserAccountService[F[_]] private (
  private val userAccount:  UserAccountAlgebra[F],
  private val emailAlgebra: EmailAlgebra[F],
)(implicit
  private val F:            Concurrent[F]
) {

  def invitationStep1(inv: UserInvitation)(implicit authCtx: AuthCtx): F[Unit] =
    for {
      regToken <- userAccount.invitationStep1(inv)
      _        <-
        emailAlgebra
          .sendEmail(
            to      = inv.email,
            //FIXME: resolve this data from an email content algebra or something
            subject = Subject(s"You have been invited to join Pure Movie Server as a :${inv.role.productPrefix}"),
            //FIXME: resolve this data from an email content algebra or something
            content = Content(s"Please click this link to finish registration: [link_to_frontend]/$regToken"),
          )
          .forkAndForget //FIXME: do recoverWith and at least delete the user registration if sending email fails.
    } yield ()

  def invitationStep2(conf: UserConfirmation): F[User] =
    for {
      user <- userAccount.invitationStep2(conf.invitationToken, conf.plainTextPassword)
    } yield user

  def resetPasswordStep1(email: Email): F[Unit] =
    for {
      resetToken <- userAccount.resetPasswordStep1(email)
      _          <-
        emailAlgebra
          .sendEmail(
            to      = email,
            subject = Subject("Password reset for Pure Movie Server"),
            content =
              Content(s"Please click the following link to reset your account password: [link_to_FE]$resetToken"),
          )
          .forkAndForget
    } yield ()

  def resetPasswordStep2(pwr: PasswordResetCompletion): F[Unit] =
    userAccount.resetPasswordStep2(pwr.token, pwr.newPws)

}

object UserAccountService {

  def resource[F[_]: Concurrent](
    userAccount:  UserAccountAlgebra[F],
    emailAlgebra: EmailAlgebra[F],
  ): Resource[F, UserAccountService[F]] = Resource.pure[F, UserAccountService[F]](
    new UserAccountService[F](
      userAccount,
      emailAlgebra,
    )
  )
}
