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
import phms.db.*
import phms.logger.Logging

/** @author Lorand Szakacs, https://github.com/lorandszakacs
  * @since 20 Jun 2018
  */
trait UserAlgebra[F[_]] {

  def findUser(id: UserID)(implicit auth: AuthCtx): F[Option[User]]

}

object UserAlgebra {

  def resource[F[_]](using
    dbPool:  DBPool[F],
    F:       MonadCancelThrow[F],
    time:    Time[F],
    r:       Random[F],
    sr:      SecureRandom[F],
    logging: Logging[F],
  ): Resource[F, UserAlgebra[F]] =
    Resource.pure[F, UserAlgebra[F]](new UserAlgebraImpl())
}
