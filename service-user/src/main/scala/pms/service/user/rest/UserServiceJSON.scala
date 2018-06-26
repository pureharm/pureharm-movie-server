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
trait UserServiceJSON extends PMSJson {

  implicit val userRegistrationCirceCodec: Codec[UserRegistration] = ???

  implicit val userCirceCodec: Codec[User] = ???

  implicit val pwResetReqCirceCodec: Codec[PasswordResetRequest] =
    ???

  implicit val pwResetComCirceCodec: Codec[PasswordResetCompletion] =
    ???

  implicit val authenticationTokenCirceCodec: Codec[AuthenticationToken] =
    ???

  implicit val authCtxCirceCodec: Codec[AuthCtx] =
    ???
}
