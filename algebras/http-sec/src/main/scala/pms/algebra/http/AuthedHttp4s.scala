package pms.algebra.http

import busymachines.core._

import pms.effects._
import pms.effects.implicits._
import pms.algebra.user._

import org.http4s._
import org.http4s.dsl._
import org.http4s.server._
import org.http4s.util.CaseInsensitiveString

/**
  *
  * @author Lorand Szakacs, https://github.com/lorandszakacs
  * @since 26 Jun 2018
  *
  */
object AuthedHttp4s {

  def userTokenAuthMiddleware[F[_]: Async](authAlgebra: UserAuthAlgebra[F]): AuthMiddleware[F, AuthCtx] =
    AuthMiddleware(verifyToken[F](authAlgebra), onFailure)

  private val `X-Auth-Token` = CaseInsensitiveString("X-AUTH-TOKEN")

  private val challenges: NonEmptyList[Challenge] = NonEmptyList.of(
    Challenge(
      scheme = "Basic",
      realm  = "Go to POST /pms/api/user/login to get valid token",
    )
  )

  private val wwwHeader = headers.`WWW-Authenticate`(challenges)

  private def onFailure[F[_]: Async]: AuthedService[Throwable, F] = Kleisli { _: AuthedRequest[F, Throwable] =>
    val fdsl = Http4sDsl[F]
    import fdsl._
    OptionT.liftF(Unauthorized(wwwHeader))
  }

  private def verifyToken[F[_]: Async](authAlgebra: UserAuthAlgebra[F]): Kleisli[F, Request[F], Attempt[AuthCtx]] =
    Kleisli { req: Request[F] =>
      val optHeader = req.headers.get(`X-Auth-Token`)
      optHeader match {
        case None         =>
          Attempt
            .raiseError[AuthCtx](UnauthorizedFailure(s"No ${`X-Auth-Token`} provided"))
            .pure[F]
        case Some(header) =>
          authAlgebra
            .authenticate(AuthenticationToken(header.value))
            .map(Attempt.pure)
            .handleError(Attempt.raiseError)
      }
    }
}
