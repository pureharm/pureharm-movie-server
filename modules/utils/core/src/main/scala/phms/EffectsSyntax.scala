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

import scala.concurrent.duration._

/** @author Lorand Szakacs, https://github.com/lorandszakacs
  * @since 08 May 2019
  */
object EffectsSyntax {

  trait Implicits {

    implicit def pureharmTemporalOps[F[_], A](fa: F[A]): PHMSTemporalOps[F, A] =
      new PHMSTemporalOps[F, A](fa)
  }

  final class PHMSTemporalOps[F[_], A](val fa: F[A]) extends AnyVal {

    def fixedTime(maxDuration: FiniteDuration)(implicit temporal: Temporal[F]): F[A] = {
      val attempted: F[Attempt[A]] = fa.attempt
      for {
        timed: (FiniteDuration, Attempt[A]) <- temporal.timed(attempted)
        (duration, attempt) = timed
        _      <- temporal.sleep((maxDuration - duration).max(0.seconds))
        result <- attempt.liftTo[F]
      } yield result
    }
  }
}
