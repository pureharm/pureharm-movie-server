package pms.algebra.http

import org.http4s._
import org.http4s.dsl._
import org.http4s.server._
import org.typelevel.ci.CIString
import pms.algebra.user._
import pms.core._

/**
  *
  * @author Lorand Szakacs, https://github.com/lorandszakacs
  * @since 26 Jun 2018
  *
  */
object AuthedHttp4s {

  def userTokenAuthMiddleware[F[_]: Async](authAlgebra: UserAuthAlgebra[F]): Resource[F, AuthMiddleware[F, AuthCtx]] = {
    val tokenVerification: Kleisli[F, Request[F], Attempt[AuthCtx]] = verifyToken[F](authAlgebra)
    AuthMiddleware(tokenVerification, onFailure).pure[Resource[F, *]]
  }

  private val `X-Auth-Token` = CIString("X-AUTH-TOKEN")

  private val challenges: NonEmptyList[Challenge] = NonEmptyList.of(
    Challenge(
      scheme = "Basic",
      realm  = "Go to POST /pms/api/user/login to get valid token",
    )
  )

  private val wwwHeader = headers.`WWW-Authenticate`(challenges)

  private def onFailure[F[_]: Async]: AuthedRoutes[Throwable, F] =
    Kleisli[OptionT[F, *], AuthedRequest[F, Throwable], Response[F]] { _: AuthedRequest[F, Throwable] =>
      val fdsl = Http4sDsl[F]
      import fdsl._
      OptionT.liftF[F, Response[F]](Unauthorized(wwwHeader))
    }

  private def verifyToken[F[_]: Async](authAlgebra: UserAuthAlgebra[F]): Kleisli[F, Request[F], Attempt[AuthCtx]] =
    Kleisli { req: Request[F] =>
      val optHeader = req.headers.get(`X-Auth-Token`)
      optHeader match {
        case None =>
          Fail.unauthorized(s"No ${`X-Auth-Token`} provided").raiseError[Attempt, AuthCtx].pure[F]
        case Some(headers: NEList[Header.Raw]) =>
          //TODO: ensure there is only one such header
          if (headers.size != 1)
            Fail
              .unauthorized(s"Found multiple ${`X-Auth-Token`} headers. Please provide only one.")
              .raiseError[Attempt, AuthCtx]
              .pure[F]
          else {
            authAlgebra
              .authenticate(AuthenticationToken(headers.head.value))
              .map(_.pure[Attempt])
              .handleError(_.raiseError[Attempt, AuthCtx])
          }

      }
    }
}
