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

import org.http4s.HttpRoutes
import phms.stack.http.AuthCtxRoutes
import phms.algebra.user.{UserAlgebra, UserAuthAlgebra}
import phms.*
import phms.organizer.user.UserAccountOrganizer

trait UserAPI[F[_]] {
  def authedRoutes: AuthCtxRoutes[F]
  def routes:       HttpRoutes[F]
}

object UserAPI {

  def resource[F[_]](
    userAlgebra:          UserAlgebra[F],
    userAuthAlgebra:      UserAuthAlgebra[F],
    userAccountOrganizer: UserAccountOrganizer[F],
  )(using Temporal[F], Defer[F]): Resource[F, UserAPI[F]] =
    for {
      userRoutes        <- Resource.pure[F, UserRoutes[F]](new UserRoutes[F](userAlgebra))
      userLoginRoutes   <- Resource.pure[F, UserLoginRoutes[F]](new UserLoginRoutes[F](userAuthAlgebra))
      userAccountRoutes <- Resource.pure[F, UserAccountRoutes[F]](new UserAccountRoutes[F](userAccountOrganizer))
    } yield new UserAPI[F] {

      override def authedRoutes: AuthCtxRoutes[F] =
        NonEmptyList.of(userRoutes.authedRoutes, userAccountRoutes.authedRoutes).reduceK

      override def routes: HttpRoutes[F] = NonEmptyList.of(userAccountRoutes.routes, userLoginRoutes.routes).reduceK
    }

}
