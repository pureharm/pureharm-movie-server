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

package phms.time

import phms.*

trait SproutTimestamp extends Sprout[OffsetDateTime] {
  def now[F[_]](implicit F:      Applicative[F], t: Time[F]): F[Type] = t.now.map(this.newType)
  def tomorrow[F[_]](implicit F: Applicative[F], t: Time[F]): F[Type] = t.now.map(n => this.newType(n.plusDays(1)))

  def isInPast[F[_]](that: Type)(implicit F: Applicative[F], t: Time[F]): F[Boolean] =
    t.now.map(now => now.isAfter(oldType(that)))
}

trait SproutSubTimestamp extends SproutSub[OffsetDateTime] {
  def now[F[_]](implicit F: Applicative[F], t: Time[F]): F[Type] = t.now.map(this.newType)
}
