package pms.algebra

import pms.core._

/**
  *
  * @author Lorand Szakacs, https://github.com/lorandszakacs
  * @since 20 Jun 2018
  *
  */
package object user {
  object UserID extends PhantomType[Long]
  type UserID = UserID.Type

  object UserRegistrationToken extends PhantomType[String]
  type UserRegistrationToken = UserRegistrationToken.Type

  object PasswordResetToken extends PhantomType[String]
  type PasswordResetToken = UserRegistrationToken.Type

  object AuthenticationToken extends PhantomType[String]
  type AuthenticationToken = AuthenticationToken.Type

  type UserModuleAlgebra[F[_]] = UserAuthAlgebra[F] with UserAlgebra[F] with UserAccountAlgebra[F]
}
