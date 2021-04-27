/*
 * Copyright 2021 BusyMachines
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package phms.organizer.user

import phms.*
import phms.kernel.*
import phms.port.email.*
import phms.algebra.user.*
import phms.port.email.EmailPort

/** @author Lorand Szakacs, https://github.com/lorandszakacs
  * @since 26 Jun 2018
  */
final class UserAccountOrganizer[F[_]] private (
  private val userAccount: UserAccountAlgebra[F],
  private val emailPort:   EmailPort[F],
)(using F: Concurrent[F]) {

  def invitationStep1(inv: UserInvitation)(using authCtx: AuthCtx): F[Unit] =
    for {
      inviteToken <- userAccount.invitationStep1(inv)
      _           <-
        //TODO: maybe it's a better idea to only write the invitation to DB if we have confirmation that email was sent
        emailPort
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
        emailPort
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

object UserAccountOrganizer {

  def resource[F[_]: Concurrent](
    userAccount: UserAccountAlgebra[F],
    emailPort:   EmailPort[F],
  ): Resource[F, UserAccountOrganizer[F]] = Resource.pure[F, UserAccountOrganizer[F]](
    new UserAccountOrganizer[F](
      userAccount = userAccount,
      emailPort   = emailPort,
    )
  )
}
