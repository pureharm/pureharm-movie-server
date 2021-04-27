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

package phms.algebra

import phms.*
import phms.time.*

/** @author Lorand Szakacs, https://github.com/lorandszakacs
  * @since 20 Jun 2018
  */
package object user {
  type UserID = UserID.Type
  object UserID extends SproutUUID

  type UserInviteToken = UserInviteToken.Type
  object UserInviteToken extends Sprout[String]

  type PasswordResetToken = PasswordResetToken.Type
  object PasswordResetToken extends Sprout[String]

  type AuthenticationToken = AuthenticationToken.Type
  object AuthenticationToken extends Sprout[String]

  type UserAuthExpiration = UserAuthExpiration.Type
  object UserAuthExpiration extends SproutTimestamp

  type UserInviteExpiration = UserInviteExpiration.Type
  object UserInviteExpiration extends SproutTimestamp

  type UserModuleAlgebra[F[_]] = UserAuthAlgebra[F] & UserAlgebra[F] & UserAccountAlgebra[F]
}
