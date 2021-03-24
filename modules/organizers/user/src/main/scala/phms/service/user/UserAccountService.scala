package phms.service.user

import phms._
import phms.kernel._
import phms.email._
import phms.algebra.user._

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
      inviteToken <- userAccount.invitationStep1(inv)
      _           <-
        //TODO: maybe it's a better idea to only write the invitation to DB if we have confirmation that email was sent
        emailAlgebra
          .sendEmail(
            to      = inv.email,
            //FIXME: resolve this data from an email content algebra or something
            subject = Subject(s"You have been invited to join Pure Movie Server as a :${inv.role.productPrefix}"),
            //FIXME: resolve this data from an email content algebra or something
            content = Content(s"Please click this link to finish registration: [link_to_frontend]/$inviteToken"),
          ) { case Outcome.Errored(_) | Outcome.Canceled() => userAccount.undoInvitationStep1(inviteToken) }
    } yield ()

  def invitationStep2(conf: UserConfirmation): F[User] =
    for {
      user <- userAccount.invitationStep2(conf.invitationToken, conf.plainTextPassword)
    } yield user

  def resetPasswordStep1(email: Email): F[Unit] =
    for {
      resetToken <- userAccount.resetPasswordStep1(email)
      //TODO: maybe it's a better idea to only write the password reset token to DB if we have confirmation that the email was sent
      _          <-
        emailAlgebra
          .sendEmail(
            to      = email,
            subject = Subject("Password reset for Pure Movie Server"),
            content =
              Content(s"Please click the following link to reset your account password: [link_to_FE]$resetToken"),
          ) { case Outcome.Errored(_) | Outcome.Canceled() => userAccount.undoPasswordResetStep1(email).attempt.void }
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
