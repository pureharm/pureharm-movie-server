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

package phms.server.bootstrap

import phms._
import phms.kernel._
import phms.logger._
import phms.algebra.user._

/** This should be used only in development, and testing!
  *
  * @author Lorand Szakacs, https://github.com/lorandszakacs
  * @since 13 Jul 2018
  */
final private[bootstrap] class ServerBootstrapAlgebra[F[_]] private (
  private val uca: UserAccountAlgebra[F],
  private val uba: UserAccountBootstrapAlgebra[F],
)(implicit F:      MonadThrow[F], logging: Logging[F]) {

  private val logger = logging.of(this)

  def bootStrapSuperAdmin(email: Email, pw: PlainTextPassword): F[User] =
    bootStrapUser(email, pw, UserRole.SuperAdmin)

  def bootStrapUser(email: Email, pw: PlainTextPassword, role: UserRole): F[User] =
    this.bootStrapUser(UserInvitation(email, role), pw)

  def bootStrapUser(inv: UserInvitation, pw: PlainTextPassword): F[User] =
    for {
      _     <- logger.info(
        s"BOOTSTRAP — inserting user invite: role=${inv.role.productPrefix} email=${inv.email} pw=$pw."
      )
      token <- uba.bootstrapUser(inv)
      _     <- logger.info(s"BOOTSTRAP — done inserting user invite")
      user  <- uca.invitationStep2(token, pw)
      _     <- logger.info(
        s"BOOTSTRAP — inserted user: role=${inv.role.productPrefix} email=${inv.email} pw=$pw"
      )
    } yield user
}

private[bootstrap] object ServerBootstrapAlgebra {

  def create[F[_]](
    uca:        UserAccountAlgebra[F],
    uba:        UserAccountBootstrapAlgebra[F],
  )(implicit F: MonadThrow[F], logging: Logging[F]): ServerBootstrapAlgebra[F] = new ServerBootstrapAlgebra(uca, uba)

}
