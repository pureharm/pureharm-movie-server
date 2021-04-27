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

import phms.*
import phms.algebra.user.{UserAccountAlgebra, UserAccountBootstrapAlgebra}
import phms.kernel.*
import phms.logger.Logging

/** @author Lorand Szakacs, https://github.com/lorandszakacs
  * @since 13 Jul 2018
  */
object Bootstrap {

  object superAdmin {
    def email[F[_]: MonadThrow]: F[Email]             = Email[F]("murray.bookchin@social.ecology")
    def passw[F[_]: MonadThrow]: F[PlainTextPassword] = PlainTextPassword[F]("OldManYellsAtAnarchism")

    //the concatenated base64($email:$passw) string, on hand, in case needed
    val BasicAuthEncoding: String =
      "bXVycmF5LmJvb2tjaGluQHNvY2lhbC5lY29sb2d5Ok9sZE1hblllbGxzQXRBbmFyY2hpc20K"
  }

  def bootstrap[F[_]](
    uca:        UserAccountAlgebra[F],
    uba:        UserAccountBootstrapAlgebra[F],
  )(using F: MonadThrow[F], logging: Logging[F]): F[Unit] =
    for {

      usb   <- ServerBootstrapAlgebra.create[F](uca, uba).pure[F]
      email <- superAdmin.email[F]
      passw <- superAdmin.passw[F]
      _     <- logging.named("bootstrap").info(s"bootstrapping super-admin user w/ email: $email")
      _     <- usb.bootStrapSuperAdmin(email, passw).attempt.void
    } yield ()

}
