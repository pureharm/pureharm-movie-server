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
import phms.*
import phms.time.*
import phms.kernel.*
import phms.db.*
import phms.logger.Logging

/** @author Lorand Szakacs, https://github.com/lorandszakacs
  * @since 20 Jun 2018
  */
abstract class UserAuthAlgebra[F[_]: MonadThrow] {

  def authenticate(email: Email, pw: PlainTextPassword): F[AuthCtx]

  def authenticate(token: AuthenticationToken): F[AuthCtx]

  final def promoteUser(id: UserID, newRole: UserRole)(implicit auth: AuthCtx): F[Unit] =
    authorizeGTERoleThan(newRole)(promoteUserOP(id, newRole))

  /** Lowest level of authorization, essentially anyone
    * who is logged in can perform the given op.
    *
    * @param op
    *   The operation that we want to guard with
    *   certain user priviliges.
    */
  final def authorizeNewbie[A](op: => F[A])(implicit auth: AuthCtx): F[A] =
    authorizeGTERoleThan(UserRole.Newbie)(op)

  /** Requires member to have priviliges from [[UserRole.Member]] upwards
    *
    * @param op
    *   The operation that we want to guard with
    *   certain user priviliges.
    */
  final def authorizeMember[A](op: => F[A])(implicit auth: AuthCtx): F[A] =
    authorizeGTERoleThan(UserRole.Member)(op)

  /** Requires member to have priviliges from [[UserRole.Curator]] upwards
    *
    * @param op
    *   The operation that we want to guard with
    *   certain user priviliges.
    */
  final def authorizeCurator[A](op: => F[A])(implicit auth: AuthCtx): F[A] =
    authorizeGTERoleThan(UserRole.Curator)(op)

  /** Requires member to have priviliges from [[UserRole.SuperAdmin]] upwards
    *
    * @param op
    *   The operation that we want to guard with
    *   certain user priviliges.
    */
  final def authorizeSuperAdmin[A](op: => F[A])(implicit auth: AuthCtx): F[A] =
    authorizeGTERoleThan(UserRole.SuperAdmin)(op)

  protected[user] def promoteUserOP(id: UserID, newRole: UserRole): F[Unit]

  final protected[user] def authorizeGTERoleThan[A](minRole: UserRole)(op: => F[A])(implicit auth: AuthCtx): F[A] =
    if (auth.user.role >= minRole)
      op
    else
      Fail.unauthorized("User not authorized to perform this action").raiseError[F, A]
}

object UserAuthAlgebra {

  def resource[F[_]](using
    dbPool:  DBPool[F],
    F:       MonadCancelThrow[F],
    time:    Time[F],
    r:       Random[F],
    sr:      SecureRandom[F],
    logging: Logging[F],
  ): Resource[F, UserAuthAlgebra[F]] =
    Resource.pure[F, UserAuthAlgebra[F]](new UserAlgebraImpl[F]())
}
