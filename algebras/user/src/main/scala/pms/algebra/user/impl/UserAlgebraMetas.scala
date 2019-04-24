package pms.algebra.user.impl

import doobie._
import doobie.implicits._
import cats.implicits._
import pms.algebra.user._
import pms.core._
import pms.effects._

/**
  *
  * @author Lorand Szakacs, https://github.com/lorandszakacs
  * @since 24 Apr 2019
  *
  */
private[impl] object UserAlgebraMetas {

  implicit val userIDMeta: Meta[UserID] = Meta[Long].imap(UserID.spook)(UserID.despook)

  implicit val authenticationTokenMeta: Meta[AuthenticationToken] =
    Meta[String].imap(AuthenticationToken.spook)(AuthenticationToken.despook)

  implicit val userRegistrationTokenMeta: Meta[UserRegistrationToken] =
    Meta[String].imap(UserRegistrationToken.spook)(UserRegistrationToken.despook)

  implicit val passwordResetTokenMeta: Meta[PasswordResetToken] =
    Meta[String].imap(PasswordResetToken.spook)(PasswordResetToken.despook)

  implicit val emailMeta: Meta[Email] =
    Meta[String].imap(Email.apply(_).unsafeGet())(_.plainTextEmail)

  implicit val pwdMeta: Meta[PlainTextPassword] =
    Meta[String].imap(PlainTextPassword.apply(_).unsafeGet())(_.plainText)

  implicit val userRoleMeta: Meta[UserRole] =
    Meta[String].imap(UserRole.fromName(_).unsafeGet())(_.toString)

  implicit val userComposite: Read[User] =
    Read[(UserID, Email, UserRole)]
      .imap((t: (UserID, Email, UserRole)) => User(t._1, t._2, t._3))((u: User) => (u.id, u.email, u.role))



}
