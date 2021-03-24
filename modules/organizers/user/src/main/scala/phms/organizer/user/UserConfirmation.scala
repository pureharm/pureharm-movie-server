package phms.organizer.user

import phms.kernel._
import phms.algebra.user._

/** @author Lorand Szakacs, https://github.com/lorandszakacs
  * @since 24 Apr 2019
  */
final case class UserConfirmation(
  invitationToken:   UserInviteToken,
  plainTextPassword: PlainTextPassword,
)
