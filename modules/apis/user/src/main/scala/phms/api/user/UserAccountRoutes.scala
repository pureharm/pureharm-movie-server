package phms.api.user

import org.http4s._
import org.http4s.dsl._
import phms.stack.http._
import phms.algebra.user._
import phms._

import phms.organizer.user._

/** @author Lorand Szakacs, https://github.com/lorandszakacs
  * @since 26 Jun 2018
  */
final class UserAccountRoutes[F[_]](
  private val userOrganizer: UserAccountOrganizer[F]
)(implicit
  val F:                     Concurrent[F],
  val D:                     Defer[F],
) extends Http4sDsl[F] with UserRoutesJSON {

  private val userInvitationStep1Routes: AuthCtxRoutes[F] = AuthCtxRoutes[F] {
    case (req @ POST -> Root / "user_invitation") as user =>
      for {
        reg  <- req.as[UserInvitation]
        _    <- userOrganizer.invitationStep1(reg)(user)
        resp <- Created()
      } yield resp
  }

  private val userInvitationStep2Routes: HttpRoutes[F] = HttpRoutes.of[F] {
    case req @ PUT -> Root / "user_invitation" / "confirmation" =>
      for {
        conf <- req.as[UserConfirmation]
        user <- userOrganizer.invitationStep2(conf)
        resp <- Ok(user)
      } yield resp
  }

  private val userPasswordResetRoutes: HttpRoutes[F] = HttpRoutes.of[F] {
    case req @ POST -> Root / "user" / "password_reset" / "request" =>
      for {
        pwr  <- req.as[PasswordResetRequest]
        _    <- userOrganizer.resetPasswordStep1(pwr.email)
        resp <- Created()
      } yield resp

    case req @ POST -> Root / "user" / "password_reset" / "completion" =>
      for {
        pwc  <- req.as[PasswordResetCompletion]
        _    <- userOrganizer.resetPasswordStep2(pwc)
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
