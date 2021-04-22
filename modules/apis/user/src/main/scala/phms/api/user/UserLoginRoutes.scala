/*
 * Copyright 2021 BusyMachines
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package phms.api.user

import org.http4s.dsl.*
import org.http4s.*
import phms.stack.http.{*, given}
import phms.algebra.user.*
import phms.*
import phms.kernel.*

import scala.concurrent.duration.*

/** @author Lorand Szakacs, https://github.com/lorandszakacs
  * @since 26 Jun 2018
  */
final class UserLoginRoutes[F[_]](
  private val userAuthAlgebra: UserAuthAlgebra[F]
)(implicit temporal:           Temporal[F], D: Defer[F])
  extends Http4sDsl[F] with UserRoutesJSON {

  /** User/password gets transmitted in the ``Authorization`` header using
    * the Basic authentication scheme. More details here:
    *   [[https://tools.ietf.org/html/rfc7617#page-5 RFC-7617]]
    *
    * header example
    * {{{
    *   Authorization: Basic QWxhZGRpbjpvcGVuIHNlc2FtZQ==
    * }}}
    * structure of string after 'Basic'
    * {{{
    *   base64Encoding($user:$password)
    * }}}
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
            """No valid 'Authorization' header. Please ensure that it includes 'Basic $bearertoken' w/ only one single space after Basic.""".stripMargin
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
      Ok(
        findBasicAuth(req.headers).flatMap(logInWithUserNamePassword).minTime(2.seconds)
      )
    }

  val routes: HttpRoutes[F] = loginRoutes

}
