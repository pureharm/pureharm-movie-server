package pms.rest.user

import org.http4s._
import org.http4s.dsl._
import pms.algebra.http._
import pms.algebra.user._
import pms._

import pms.service.user._

/** @author Lorand Szakacs, https://github.com/lorandszakacs
  * @since 26 Jun 2018
  */
final class UserAccountRoutes[F[_]](
  private val userService: UserAccountService[F]
)(implicit
  val F:                   Async[F]
) extends Http4sDsl[F] with UserRoutesJSON {

  private val userInvitationStep1Routes: AuthCtxRoutes[F] = AuthCtxRoutes[F] {
    case (req @ POST -> Root / "user_invitation") as user =>
      for {
        reg  <- req.as[UserInvitation]
        _    <- userService.invitationStep1(reg)(user)
        resp <- Created()
      } yield resp
  }

  private val userInvitationStep2Routes: HttpRoutes[F] = HttpRoutes.of[F] {
    case req @ PUT -> Root / "user_invitation" / "confirmation" =>
      for {
        conf <- req.as[UserConfirmation]
        user <- userService.invitationStep2(conf)
        resp <- Ok(user)
      } yield resp
  }

  private val userPasswordResetRoutes: HttpRoutes[F] = HttpRoutes.of[F] {
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

  val routes: HttpRoutes[F] =
    NonEmptyList
      .of[HttpRoutes[F]](
        userInvitationStep2Routes,
        userPasswordResetRoutes,
      )
      .reduceK

  val authedRoutes: AuthCtxRoutes[F] =
    userInvitationStep1Routes

}
