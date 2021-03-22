package phms.algebra.user

import phms.kernel._

/** @author Lorand Szakacs, https://github.com/lorandszakacs
  * @since 20 Jun 2018
  */
final case class User(
  id:    UserID,
  email: Email,
  role:  UserRole,
)
