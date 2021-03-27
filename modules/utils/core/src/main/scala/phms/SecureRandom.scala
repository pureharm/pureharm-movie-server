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

package phms

sealed trait SecureRandom[F[_]] {
  def sync: Sync[F]
  def nextBytes(n:         Int): F[Array[Byte]]
  def nextBytesAsBase64(n: Int): F[String]
}

object SecureRandom {

  def resource[F[_]](implicit F: Sync[F]): Resource[F, SecureRandom[F]] =
    Resource.eval[F, SecureRandom[F]] {
      cats.effect.std.Random.javaSecuritySecureRandom(8).map { (r: Random[F]) =>
        new SecureRandom[F] {
          override def sync: Sync[F] = F
          override def nextBytes(n:         Int): F[Array[Byte]] = r.nextBytes(n)
          override def nextBytesAsBase64(n: Int): F[String]      =
            Stream
              .evalSeq(this.nextBytes(n).map(_.toSeq))
              .through(fs2.text.base64.encode[F])
              .compile
              .string
        }
      }
    }
}
