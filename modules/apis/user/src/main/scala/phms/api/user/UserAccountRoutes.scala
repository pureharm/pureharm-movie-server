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

import org.http4s.*
import org.http4s.dsl.*
import phms.stack.http.{*, given}
import phms.algebra.user.*
import phms.*

import phms.organizer.user.*

/** @author Lorand Szakacs, https://github.com/lorandszakacs
  * @since 26 Jun 2018
  */
final class UserAccountRoutes[F[_]](
  private val userOrganizer: UserAccountOrganizer[F]
)(using Concurrent[F], Defer[F]) extends Http4sDsl[F] with UserRoutesJSON {

  private val userInvitationStep1Routes: AuthCtxRoutes[F] = AuthCtxRoutes[F] {
    case (req @ POST -> Root / "user_invitation") `as` user =>
      for {
        reg  <- req.as[UserInvitation]
        _    <- userOrganizer.invitationStep1(reg)(using user)
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
