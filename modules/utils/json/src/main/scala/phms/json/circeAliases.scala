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

package phms.json

type Codec[A] = io.circe.Codec[A]
val Codec: io.circe.Codec.type = io.circe.Codec

type Encoder[A] = io.circe.Encoder[A]
val Encoder: io.circe.Encoder.type = io.circe.Encoder

type Decoder[A] = io.circe.Decoder[A]
val Decoder: io.circe.Decoder.type = io.circe.Decoder

  /** see:
    * https://github.com/circe/circe/blob/master/modules/generic/shared/src/main/scala-3/io/circe/generic/semiauto.scala
    */
object derive {
  import scala.deriving.Mirror
  inline final def decoder[A](using inline A: Mirror.Of[A]): Decoder[A] = Decoder.derived[A]
  inline final def encoder[A](using inline A: Mirror.Of[A]): Encoder.AsObject[A] = Encoder.AsObject.derived[A]
  inline final def codec[A](using inline A: Mirror.Of[A]): Codec.AsObject[A] = Codec.AsObject.derived[A]
}

//  given[O, N](using ot: OldType[O, N], enc: Encoder[O]):          Encoder[N] = enc.contramap(ot.oldType)
//  given[O, N](using nt: NewType[O, N], dec: Decoder[O]):          Decoder[N] = dec.imap(nt.newType)(nt.oldType)
//  given[O, N](using nt: RefinedTypeThrow[O, N], dec: Decoder[O]): Decoder[N] = dec.emapTry(o => nt.newType[Try](o))
