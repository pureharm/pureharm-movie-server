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
private[impl] object UserAlgebraMetas {

  implicit val userIDMeta: Meta[UserID] =
    Meta[Long].imap(UserID.spook)(UserID.despook)

  implicit val authenticationTokenMeta: Meta[AuthenticationToken] =
    Meta[String].imap(AuthenticationToken.spook)(AuthenticationToken.despook)

  implicit val userRegistrationTokenMeta: Meta[UserInviteToken] =
    Meta[String].imap(UserRegistrationToken.spook)(UserRegistrationToken.despook)

  implicit val passwordResetTokenMeta: Meta[PasswordResetToken] =
    Meta[String].imap(PasswordResetToken.spook)(PasswordResetToken.despook)

  implicit val emailGet: Get[Email] =
    Get[String].temap(e => Email(e).leftMap(_.getMessage))

  implicit val emailPut: Put[Email] =
    Put[String].contramap(_.plainTextEmail)

  implicit val pwdGet: Get[PlainTextPassword] =
    Get[String].temap(e => PlainTextPassword(e).leftMap(_.getMessage))

  implicit val pwdPut: Put[PlainTextPassword] =
    Put[String].contramap(_.plainText)

  implicit val userRoleGet: Get[UserRole] =
    Get[String].temap(n => UserRole.fromName(n).leftMap(_.getMessage))

  implicit val userRolePut: Put[UserRole] =
    Put[String].contramap(_.productPrefix)

  implicit val userComposite: Read[User] =
    Read[(UserID, Email, UserRole)]
      .imap((t: (UserID, Email, UserRole)) => User(t._1, t._2, t._3))((u: User) => (u.id, u.email, u.role))

}
