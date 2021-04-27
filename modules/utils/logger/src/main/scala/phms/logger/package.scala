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

import org.typelevel.log4cats

/** @author Lorand Szakacs, https://github.com/lorandszakacs
  * @since 25 Mar 2019
  */
package object logger {
  type Logger[F[_]] = log4cats.SelfAwareStructuredLogger[F]
  val Logger: log4cats.SelfAwareStructuredLogger.type = log4cats.SelfAwareStructuredLogger

  val Slf4jLogger: log4cats.slf4j.Slf4jLogger.type = log4cats.slf4j.Slf4jLogger
}
