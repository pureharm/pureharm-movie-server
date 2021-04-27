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

import fs2.Chunk
import io.circe.{Json, Printer}
import phms.*
import phms.json.{*, given}
import org.http4s.*
import org.http4s.headers.`Content-Type`
import org.http4s.{EntityDecoder, EntityEncoder}

/** You need to have this in scope if you want "seamless" serializing/deserializing
  * to/from JSON in your HttpRoutes endpoints.
  *
  * @author Lorand Szakacs, https://github.com/lorandszakacs
  * @since 26 Jun 2018
  */
trait Http4sCirceInstances {

  import Http4sCirceInstances.*

  /** This code was copied from [[org.http4s.circe.CirceInstances#jsonEncoderWithPrinter]]
    * Ideally, we would have done directly:
    * {{{
    *   circeInstance.jsonEncoderOf[F, T]
    * }}}
    * But that throws us into an infinit loop because the implicit picks itself up.
    *
    * @return
    */
  given[F[_], T: Encoder]: EntityEncoder[F, T] =
    EntityEncoder[F, Chunk[Byte]]
      .contramap[Json] { json =>
        val bytes = printer.printToByteBuffer(json)
        Chunk.byteBuffer(bytes)
      }
      .withContentType(`Content-Type`(MediaType.application.json))
      .contramap(t => Encoder.apply[T].apply(t))

  given[F[_]: Concurrent, T: Decoder]: EntityDecoder[F, T] =
    ??? //circeInstances.jsonOf[F, T]

}

object Http4sCirceInstances {
  private val printer: Printer = Printer.noSpaces.copy(dropNullValues = true)
}
