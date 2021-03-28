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

package phms.api.movie

import phms.algebra.movie.*
import phms.json.{*, given}

/** @author Lorand Szakacs, https://github.com/lorandszakacs
  * @since 25 Jun 2018
  */
object MovieRoutesJSON extends MovieRoutesJSON

trait MovieRoutesJSON {

  given movieCirceCodec: Codec[Movie] = derive.codec[Movie]

  given movieCreationCirceCodec: Codec[MovieCreation] =
    derive.codec[MovieCreation]
}
