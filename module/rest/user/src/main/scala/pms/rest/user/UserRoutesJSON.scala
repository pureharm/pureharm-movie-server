package pms.rest.user

import pms._
import pms.algebra.user._
import pms.json._
import pms.service.user._

/** @author Lorand Szakacs, https://github.com/lorandszakacs
  * @since 26 Jun 2018
  */
trait UserRoutesJSON {

  implicit val userRoleCirceCodec: Codec[UserRole] = Codec.from(
    Decoder[String].emapTry(s => UserRole.fromName[Try](s)),
    Encoder[String].contramap(_.toName),
  )

  implicit val userInvitationCirceCodec: Codec[UserInvitation] =
    derive.codec[UserInvitation]

  implicit val userConfirmationCirceCodec: Codec[UserConfirmation] =
    derive.codec[UserConfirmation]

  implicit val userCirceCodec: Codec[User] = derive.codec[User]

  implicit val pwResetReqCirceCodec: Codec[PasswordResetRequest] =
    derive.codec[PasswordResetRequest]

  implicit val pwResetComCirceCodec: Codec[PasswordResetCompletion] =
    derive.codec[PasswordResetCompletion]

  implicit val authCtxCirceCodec: Codec[AuthCtx] = derive.codec[AuthCtx]

}
