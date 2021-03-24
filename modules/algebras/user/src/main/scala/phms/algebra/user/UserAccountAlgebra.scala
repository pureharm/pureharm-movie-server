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

import phms.algebra.user.impl.UserAlgebraImpl
import phms._
import phms.time._
import phms.kernel._
import phms.db._

/** @author Lorand Szakacs, https://github.com/lorandszakacs
  * @since 20 Jun 2018
  */
trait UserAccountAlgebra[F[_]] {

  implicit protected def monadThrow: MonadThrow[F]
  protected def authAlgebra:         UserAuthAlgebra[F]

  final def invitationStep1(
    inv:  UserInvitation
  )(implicit
    auth: AuthCtx
  ): F[UserInviteToken] =
    authAlgebra.authorizeGTERoleThan(inv.role)(registrationStep1Impl(inv))

  protected[user] def registrationStep1Impl(inv: UserInvitation): F[UserInviteToken]

  def undoInvitationStep1(token: UserInviteToken): F[Unit]

  def invitationStep2(token: UserInviteToken, pw: PlainTextPassword): F[User]

  def resetPasswordStep1(email: Email): F[PasswordResetToken]

  /** Needed in case sending the password reset email fails
    */
  def undoPasswordResetStep1(email: Email): F[Unit]

  def resetPasswordStep2(token: PasswordResetToken, newPassword: PlainTextPassword): F[Unit]
}

object UserAccountAlgebra {

  def resource[F[_]](implicit
    dbPool: DBPool[F],
    F:      MonadCancelThrow[F],
    time:   Time[F],
    r:      Random[F],
    sr:     SecureRandom[F],
  ): Resource[F, UserAccountAlgebra[F]] =
    Resource.pure[F, UserAccountAlgebra[F]](new UserAlgebraImpl[F]())
}
