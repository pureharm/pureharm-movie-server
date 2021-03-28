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

import cats.effect.std.Semaphore

import scala.concurrent.duration.*

/** Used to control the rate at which Fs are computed by fixing the amount of Fs
  * to be executed in the given time interval.
  *
  * @param interval  time unit between `size` consecutive requests
  * @param semaphore bounded by the number of Fs allowed to be executed in the configured `interval`
  */
final class EffectThrottler[F[_]](
  private val interval:  FiniteDuration,
  private val semaphore: Semaphore[F],
)(using F:        Temporal[F]) {

  /** Returns an F that will be "slowed" time to the configured rate
    * of execution.
    */
  def throttle[T](f: F[T]): F[T] =
    /** Basically, we can treat the semaphore permit as a resource,
      * and this way we guarantee its release, even in case of interruption.
      */
    semaphore.permit.use { _ =>
      for {
        timedAttempt <- F.timed(f.attempt)
        (duration, attempt) = timedAttempt
        _      <- if (isWithinInterval(duration)) F.sleep(interval - duration) else F.unit
        result <- attempt.liftTo[F]
      } yield result
    }

  private def isWithinInterval(duration: FiniteDuration): Boolean = duration < interval
}

object EffectThrottler {

  def resource[F[_]](
    interval:   FiniteDuration,
    amount:     Long,
  )(using Temporal[F]): Resource[F, EffectThrottler[F]] =
    Resource.eval {
      for {
        sem <- Semaphore(amount)
      } yield new EffectThrottler[F](
        interval  = interval,
        semaphore = sem,
      )
    }

}
