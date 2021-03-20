package pms.algebra.user

import pms.kernel._

/** @author Lorand Szakacs, https://github.com/lorandszakacs
  * @since 26 Jun 2018
  */
final case class UserInvitation(
  email: Email,
  role:  UserRole,
)
