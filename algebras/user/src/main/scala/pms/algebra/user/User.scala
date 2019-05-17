package pms.algebra.user

import pms.core._

/**
  *
  * @author Lorand Szakacs, https://github.com/lorandszakacs
  * @since 20 Jun 2018
  *
  */
final case class User(
  id:    UserID,
  email: Email,
  role:  UserRole,
)
