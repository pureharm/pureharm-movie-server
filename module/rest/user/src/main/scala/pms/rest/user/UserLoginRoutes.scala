package pms.rest.user

import org.http4s.dsl._
import org.http4s.{headers, _}
import pms.algebra.http._
import pms.algebra.user._
import pms.core._

/**
  *
  * @author Lorand Szakacs, https://github.com/lorandszakacs
  * @since 26 Jun 2018
  *
  */
final class UserLoginRoutes[F[_]](
  private val userAuthAlgebra: UserAuthAlgebra[F]
)(implicit
  val F:                       Async[F]
) extends Http4sDsl[F] with UserRoutesJSON {

  /**
    * User/password gets transimited in the ``Authorization``
    * Authorization: Basic QWxhZGRpbjpvcGVuIHNlc2FtZQ==
    *
    * base64Encoding($user:$password)
    */
  private def logInWithUserNamePassword(bc: BasicCredentials): F[AuthCtx] =
    for {
      email <- Email(bc.username).liftTo[F]
      ptpw  <- PlainTextPassword(bc.password).liftTo[F]
      auth  <- userAuthAlgebra.authenticate(email, ptpw)
    } yield auth

  private def findBasicAuth(hs: Headers): F[BasicCredentials] = {
    val r: Attempt[BasicCredentials] = for {
      auth:  headers.Authorization <-
        hs.get(headers.Authorization)
          .liftTo[Attempt](Fail.unauthorized("Missing Authorization header"))
      basic: BasicCredentials <- auth.credentials match {
        case Credentials.Token(AuthScheme.Basic, token) =>
          BasicCredentials(token).pure[Attempt]
        case credentials                                =>
          Fail
            .unauthorized(s"Unsupported credentials w/ AuthScheme ${credentials.authScheme}")
            .raiseError[Attempt, BasicCredentials]
      }
    } yield basic

    r.liftTo[F]
  }

  private val loginRoutes: HttpRoutes[F] =
    HttpRoutes.of[F] {
      case req @ POST -> Root / "user" / "login" =>
        Ok(findBasicAuth(req.headers).flatMap(logInWithUserNamePassword))
    }

  val routes: HttpRoutes[F] = loginRoutes

}
