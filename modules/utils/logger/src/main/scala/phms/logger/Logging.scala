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

package phms.logger

import phms.*

trait Logging[F[_]] {
  def of(a:    AnyRef): Logger[F]
  def named(s: String): Logger[F]
}

object Logging {

  def apply[F[_]](using l: Logging[F]): Logging[F] = l

  def resource[F[_]: Sync]: Resource[F, Logging[F]] =
    new Logging[F] {
      override def of(a:    AnyRef): Logger[F] = Slf4jLogger.getLoggerFromName(a.getClass.getSimpleName.stripSuffix("$"))
      override def named(s: String): Logger[F] = Slf4jLogger.getLoggerFromName(s)
    }.pure[Resource[F, *]]
}
