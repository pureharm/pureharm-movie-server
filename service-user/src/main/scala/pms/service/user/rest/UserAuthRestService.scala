package pms.service.user.rest

import cats.data._
import cats.implicits._

import pms.core._
import pms.effects._
import pms.algebra.http._

import pms.algebra.user._
import org.http4s._
import org.http4s.dsl._

import org.http4s.server._
import org.http4s.server.middleware.authentication._

import scala.util.control.NonFatal

/**
  *
  * @author Lorand Szakacs, https://github.com/lorandszakacs
  * @since 26 Jun 2018
  *
  */
final class UserAuthRestService[F[_]](
  private val userAuthAlgebra: UserAuthAlgebra[F],
)(
  implicit val F: Async[F],
) extends Http4sDsl[F] with UserServiceJSON {

  /**
    * User/password gets transimited in the ``Authorization``
    * Authorization: Basic QWxhZGRpbjpvcGVuIHNlc2FtZQ==
    *
    * base64Encoding($user:$password)
    */
  private def logInWithUserNamePassword(bc: BasicCredentials): F[Option[AuthCtx]] = {
    val optT = for {
      email <- OptionT[F, Email](F.pure(Email(bc.username).toOption))
      ptpw  <- OptionT[F, PlainTextPassword](F.pure(PlainTextPassword(bc.password).toOption))
      auth <- OptionT[F, AuthCtx](userAuthAlgebra.authenticate(email, ptpw).map(Option.apply).recover {
               case NonFatal(_) => Option.empty[AuthCtx]
             })
    } yield auth
    optT.value
  }

  private val middleware: AuthMiddleware[F, AuthCtx] =
    BasicAuth("Basic", logInWithUserNamePassword)

  private val basicAuthService: AuthedService[AuthCtx, F] =
    AuthedService {
      case POST -> Root / "user" / "login" as authCtx => Ok(F.pure(authCtx))
    }

  val service: HttpService[F] = middleware(basicAuthService)

}
