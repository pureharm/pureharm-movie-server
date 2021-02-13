package pms.rest.user

import pms.algebra.user.{AuthCtx, User, UserInvitation, UserRole}
import pms.json.{derive, Codec, Decoder, Encoder}
import pms.service.user.{PasswordResetCompletion, PasswordResetRequest, UserConfirmation}

/**
  *
  * @author Lorand Szakacs, https://github.com/lorandszakacs
  * @since 26 Jun 2018
  *
  */
trait UserRoutesJSON {

  implicit val userRoleCirceCodec: Codec[UserRole] = Codec.from(
    Decoder[String].emap(s => UserRole.fromName(s).left.map(_.getMessage)),
    Encoder[String].contramap(ur => ur.productPrefix),
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
