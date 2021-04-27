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

package phms.config

import phms.*

trait ConfigLoader[T] {
  def configValue: ConfigValue[Effect, T]
  def load[F[_]](using F:     Config[F]): F[T]           = F.load(configValue)
  def resource[F[_]](using F: Config[F]): Resource[F, T] = Resource.eval(F.load(configValue))
}
