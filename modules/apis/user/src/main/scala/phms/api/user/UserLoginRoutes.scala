package phms.api.user

import org.http4s.dsl._
import org.http4s._
import phms.stack.http._
import phms.algebra.user._
import phms._
import phms.kernel._

/** @author Lorand Szakacs, https://github.com/lorandszakacs
  * @since 26 Jun 2018
  */
final class UserLoginRoutes[F[_]](
  private val userAuthAlgebra: UserAuthAlgebra[F]
)(implicit
  val F:                       Concurrent[F],
  val D:                       Defer[F],
) extends Http4sDsl[F] with UserRoutesJSON {

  /** User/password gets transimited in the ``Authorization``
    * Authorization: Basic QWxhZGRpbjpvcGVuIHNlc2FtZQ==
    *
    * base64Encoding($user:$password)
    */
  private def logInWithUserNamePassword(bc: BasicCredentials): F[AuthCtx] =
    for {
      email <- Email[F](bc.username)
      ptpw  <- PlainTextPassword[F](bc.password)
      auth  <- userAuthAlgebra.authenticate(email, ptpw)
    } yield auth

  private def findBasicAuth(hs: Headers): F[BasicCredentials] =
    for {
      authHeader <- hs
        .get[headers.Authorization]
        .liftTo[F](
          Fail.unauthorized(
            """|No 'Authorization' header, please include one 
               |for authentication
               |""".stripMargin
          )
        )
      basic: BasicCredentials <- authHeader.credentials match {
        case Credentials.Token(AuthScheme.Basic, token) =>
          BasicCredentials(token).pure[F]
        case credentials                                =>
          Fail
            .unauthorized(s"Unsupported credentials w/ AuthScheme ${credentials.authScheme}")
            .raiseError[F, BasicCredentials]
      }
    } yield basic

  private val loginRoutes: HttpRoutes[F] =
    HttpRoutes.of[F] { case req @ POST -> Root / "user" / "login" =>
      Ok(findBasicAuth(req.headers).flatMap(logInWithUserNamePassword))
    }

  val routes: HttpRoutes[F] = loginRoutes

}
