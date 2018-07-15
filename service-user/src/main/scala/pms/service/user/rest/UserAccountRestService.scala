package pms.service.user.rest

import cats.implicits._

import pms.effects._

import pms.algebra.user._
import pms.algebra.http._

import pms.service.user._

import org.http4s._
import org.http4s.dsl._

/**
  *
  * @author Lorand Szakacs, https://github.com/lorandszakacs
  * @since 26 Jun 2018
  *
  */
final class UserAccountRestService[F[_]](
  private val userService: UserAccountService[F]
)(
  implicit val F: Async[F]
) extends Http4sDsl[F] with UserServiceJSON {

  private object RegistrationTokenMatcher extends QueryParamDecoderMatcher[String]("registrationToken")

  private val userRegistrationStep1Service: AuthCtxService[F] = AuthCtxService[F] {
    case (req @ POST -> Root / "user_registration") as user =>
      for {
        reg  <- req.as[UserRegistration]
        _    <- userService.registrationStep1(reg)(user)
        resp <- Created()
      } yield resp
  }

  private val userRegistrationStep2Service: HttpService[F] = HttpService[F] {
    case PUT -> Root / "user_registration" / "confirmation" :? RegistrationTokenMatcher(token) =>
      for {
        user <- userService.registrationStep2(UserRegistrationToken(token))
        resp <- Ok(user)
      } yield resp
  }

  private val userPasswordResetService: HttpService[F] = HttpService[F] {
    case req @ POST -> Root / "user" / "password_reset" / "request" =>
      for {
        pwr  <- req.as[PasswordResetRequest]
        _    <- userService.resetPasswordStep1(pwr.email)
        resp <- Created()
      } yield resp

    case req @ POST -> Root / "user" / "password_reset" / "completion" =>
      for {
        pwc  <- req.as[PasswordResetCompletion]
        _    <- userService.resetPasswordStep2(pwc)
        resp <- Created()
      } yield resp
  }

  val service: HttpService[F] =
    NonEmptyList
      .of[HttpService[F]](
        userRegistrationStep2Service,
        userPasswordResetService
      )
      .reduceK

  val authedService: AuthCtxService[F] =
    userRegistrationStep1Service

}
