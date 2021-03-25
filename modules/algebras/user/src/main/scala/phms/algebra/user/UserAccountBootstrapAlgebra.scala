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

package phms.algebra.user

import phms._

/** @author Lorand Szakacs, https://github.com/lorandszakacs
  * @since 13 Jul 2018
  */
final class UserAccountBootstrapAlgebra[F[_]] private (
  private val uca: UserAccountAlgebra[F]
) {

  def bootstrapUser(inv: UserInvitation): F[UserInviteToken] =
    uca.registrationStep1Impl(inv)
}

object UserAccountBootstrapAlgebra {

  def resource[F[_]](uca: UserAccountAlgebra[F])(implicit
    F:                    MonadThrow[F]
  ): Resource[F, UserAccountBootstrapAlgebra[F]] =
    new UserAccountBootstrapAlgebra[F](uca).pure[Resource[F, *]].widen
}
