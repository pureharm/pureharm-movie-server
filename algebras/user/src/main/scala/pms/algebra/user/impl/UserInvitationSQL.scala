package pms.algebra.user.impl

import doobie._
import pms.algebra.user._
import pms.core._

/**
  *
  * @author Lorand Szakacs, https://github.com/lorandszakacs
  * @since 24 Apr 2019
  *
  */
private[impl] object UserInvitationSQL {
  final case class UserInvitationRepr(
    email:           Email,
    role:            UserRole,
    invitationToken: UserRegistrationToken,
  )
  def insert(inv: UserInvitationRepr): ConnectionIO[Unit] = ???

  def findByToken(tok: UserRegistrationToken): ConnectionIO[Option[UserInvitationRepr]] = ???

  def deleteByToken(tok: UserRegistrationToken): ConnectionIO[Unit] = ???

}
