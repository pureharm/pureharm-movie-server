package pms.service.user

import pms.algebra.user.UserInviteToken
import pms.core.PlainTextPassword

/**
  *
  * @author Lorand Szakacs, https://github.com/lorandszakacs
  * @since 24 Apr 2019
  *
  */
final case class UserConfirmation(
                                   invitationToken:   UserInviteToken,
                                   plainTextPassword: PlainTextPassword,
)
