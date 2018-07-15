package pms.service.user.rest

import busymachines.core.UnauthorizedFailure

import cats.implicits._

import pms.core._
import pms.effects._
import pms.algebra.http._
import pms.algebra.user._

import org.http4s._
import org.http4s.dsl._
import org.http4s.headers

/**
  *
  * @author Lorand Szakacs, https://github.com/lorandszakacs
  * @since 26 Jun 2018
  *
  */
final class UserLoginRestService[F[_]](
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
  private def logInWithUserNamePassword(bc: BasicCredentials): F[AuthCtx] =
    for {
      email <- rtof(Email(bc.username))
      ptpw  <- rtof(PlainTextPassword(bc.password))
      auth  <- userAuthAlgebra.authenticate(email, ptpw)
    } yield auth

  private def findBasicAuth(hs: Headers): F[BasicCredentials] = {
    val r = for {
      auth <- hs.get(headers.Authorization).asResult(UnauthorizedFailure("Missing Authorization header"))
      basic <- auth.credentials match {
                case BasicCredentials(basic) =>
                  Result.pure(basic)
                case credentials =>
                  Result.fail(UnauthorizedFailure(s"Unsupported credentials w/ AuthScheme ${credentials.authScheme}"))
              }
    } yield basic

    rtof(r)
  }

  //TODO: write bm-commons combinators that work on typeclasses
  private def rtof[T](r: Result[T]): F[T] = r match {
    case Left(value)  => F.raiseError(value.asThrowable)
    case Right(value) => F.pure(value)
  }

  private val loginService: HttpService[F] =
    HttpService[F] {
      case req @ (POST -> Root / "user" / "login") =>
        Ok(findBasicAuth(req.headers).flatMap(logInWithUserNamePassword))
    }

  val service: HttpService[F] = loginService

}
