package pms.algebra.http

import cats.data.{Kleisli, OptionT}
import cats.implicits._

import busymachines.core.Anomaly
import busymachines.core.UnauthorizedFailure

import pms.effects._
import pms.algebra.user._

import org.http4s._
import org.http4s.dsl._
import org.http4s.server._

import org.http4s.util.CaseInsensitiveString

import scala.util.control.NonFatal

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
  private val challenges: NonEmptyList[Challenge] = NonEmptyList.of(     ////ce sunt aceste challenges?
    Challenge(
      scheme = "Basic",
      realm  = "Go to POST /pms/api/user/login to get valid token",
    )
  )

  private val wwwHeader = headers.`WWW-Authenticate`(challenges)

  private def onFailure[F[_]: Async]: AuthedService[Anomaly, F] = Kleisli { _: AuthedRequest[F, Anomaly] =>   /// asa descompun tot timpul un autherService?
    val fdsl = Http4sDsl[F]
    import fdsl._           //to have access to Unauthorized
    OptionT.liftF(Unauthorized(wwwHeader)) //din F[Response[F] fac lift in OptionT[F,Response[F]] , dar Unauthorized imi returneaza un status cum  ajung la F[Response[F]] ?
  }

  private def verifyToken[F[_]: Async](authAlgebra: UserAuthAlgebra[F]): Kleisli[F, Request[F], Result[AuthCtx]] =  /// de ce am F si nu OptionT[F,?]
    Kleisli { req: Request[F] =>
      val F         = Async.apply[F]
      val optHeader = req.headers.get(`X-Auth-Token`)
      optHeader match {
        case None =>
          F.pure(Result.fail(UnauthorizedFailure(s"No ${`X-Auth-Token`} provided")))
        case Some(header) =>
          authAlgebra.authenticate(AuthenticationToken(header.value)).map(Result.pure).recover {
            case NonFatal(a: Anomaly) =>
              Result.fail(a)
            case NonFatal(a) =>
              Result.failThr(a)
          }
      }
    }
}
