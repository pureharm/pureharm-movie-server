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

    /** @param minDuration
      *   Ensures that given effect executes in _at least_ the provided
      *   time. It can take longer that, since there's no way around that
      *   problem. Also, it does not delay execution if the underlying effect
      *   is cancelled.
      */
    def minTime(minDuration: FiniteDuration)(implicit temporal: Temporal[F]): F[A] = {
      val attempted: F[Attempt[A]] = fa.attempt
      for {
        timed  <- temporal.timed(attempted)
        (duration: FiniteDuration, attempt: Attempt[A]) = timed
        _      <- temporal.sleep((minDuration - duration).max(0.seconds))
        result <- attempt.liftTo[F]
      } yield result
    }
  }
}
