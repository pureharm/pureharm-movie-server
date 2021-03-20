package pms.algebra

import pms._

/** @author Lorand Szakacs, https://github.com/lorandszakacs
  * @since 20 Jun 2018
  */
package object user {
  object UserID extends Sprout[Long] with SproutEq[Long]
  type UserID = UserID.Type

  type UserInviteToken = UserInviteToken.Type
  object UserInviteToken extends Sprout[String]

  object PasswordResetToken extends Sprout[String]
  type PasswordResetToken = PasswordResetToken.Type

  object AuthenticationToken extends Sprout[String]
  type AuthenticationToken = AuthenticationToken.Type

  type UserModuleAlgebra[F[_]] = UserAuthAlgebra[F] with UserAlgebra[F] with UserAccountAlgebra[F]
}
