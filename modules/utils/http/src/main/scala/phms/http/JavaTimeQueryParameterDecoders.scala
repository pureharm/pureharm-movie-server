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

package phms.http

import org.http4s.{ParseFailure, QueryParamDecoder}
import phms.*
import phms.time.*

/** @author Lorand Szakacs, https://github.com/lorandszakacs
  * @since 28 Jun 2018
  */
trait JavaTimeQueryParameterDecoders {

  given localDateQueryParamDecoder: QueryParamDecoder[LocalDate] =
    QueryParamDecoder.stringQueryParamDecoder.emap(s =>
      LocalDate
        .fromString[Attempt](s)
        .leftMap(t =>
          ParseFailure(
            sanitized = "Failed to parse query param for LocalDate",
            details   = t.getMessage,
          )
        )
    )
}
