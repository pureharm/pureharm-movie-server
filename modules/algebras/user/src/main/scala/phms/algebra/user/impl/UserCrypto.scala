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

package phms.algebra.user.impl

import phms._
import phms.kernel._

/** @author Lorand Szakacs, https://github.com/lorandszakacs
  * @since 22 Mar 2019
  */
private[impl] object UserCrypto {

  type BcryptPW = phms.crypto.BCryptHash

  private[impl] def generateToken[F[_], TokenType](implicit
    F:  ApplicativeThrow[F],
    sr: SecureRandom[F],
    nt: NewType[String, TokenType],
  ): F[TokenType] =
    sr.nextBytesAsBase64(64).map(nt.newType)

  private[impl] def hashPWWithBcrypt[F[_]](
    ptpw:       PlainTextPassword
  )(implicit F: MonadThrow[F], sr: SecureRandom[F]): F[BcryptPW] = phms.crypto.BCrypt.createBCrypt[F](ptpw)

  private[impl] def checkUserPassword[F[_]](
    p:          PlainTextPassword,
    hash:       UserCrypto.BcryptPW,
  )(implicit F: MonadThrow[F], sr: SecureRandom[F]): F[Boolean] = phms.crypto.BCrypt.verify[F](p, hash)
}
