package pms.service.user.rest

import pms.json._

import pms.algebra.user._
import pms.service.user._

/**
  *
  * @author Lorand Szakacs, https://github.com/lorandszakacs
  * @since 26 Jun 2018
  *
  */
trait UserRoutesJSON extends PMSJson {

  //FIXME: derive automatically
  implicit val userRoleCirceCodec:            Codec[UserRole]            = Codec.from(
    Decoder
      .apply[String]
      .emap(s => UserRole.fromName(s).left.map(_.getMessage)),
    Encoder.apply[String].contramap(ur => ur.productPrefix),
  )

  //FIXME: derive automatically
  implicit val userIDCirceCodec:              Codec[UserID]              = Codec.from[UserID](
    Decoder.apply[Long].map(UserID.spook),
    Encoder.apply[Long].contramap(UserID.despook),
  )

  //FIXME: derive automatically
  implicit val passwordResetTokenCirceCodec:  Codec[PasswordResetToken]  =
    Codec.from[PasswordResetToken](
      Decoder.apply[String].map(PasswordResetToken.spook),
      Encoder.apply[String].contramap(PasswordResetToken.despook),
    )

  //FIXME: derive automatically
  implicit val authenticationTokenCirceCodec: Codec[AuthenticationToken] =
    Codec.from(
      Decoder.apply[String].map(AuthenticationToken.spook),
      Encoder.apply[String].contramap(AuthenticationToken.despook),
    )

  //FIXME: derive automatically
  implicit val invitationTokenCirceCodec:     Codec[UserInviteToken]     =
    Codec.from(
      Decoder.apply[String].map(UserRegistrationToken.spook),
      Encoder.apply[String].contramap(UserRegistrationToken.despook),
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
