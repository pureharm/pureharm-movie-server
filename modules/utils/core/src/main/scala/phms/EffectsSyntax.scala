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

/** @author Lorand Szakacs, https://github.com/lorandszakacs
  * @since 08 May 2019
  */
object EffectsSyntax {

  trait Implicits {

    implicit def transformFAIntoFAWithSyntax[F[_]: Concurrent, A](fa: F[A]): ConcurrentFAOps[F, A] =
      new ConcurrentFAOps(fa)
  }

  class ConcurrentFAOps[F[_], A](fa: F[A])(implicit F: Concurrent[F]) {
    def forkAndForget: F[Unit] = F.start(fa).void
  }
}
