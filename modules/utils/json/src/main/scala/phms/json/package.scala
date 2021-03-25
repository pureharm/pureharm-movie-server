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

/** Simply an alias for busymachines.pureharm.json._ so that we don't have
  * to always import that as well
  *
  * @author Lorand Szakacs, https://github.com/lorandszakacs
  * @since 25 Jun 2018
  */
package object json extends JavaTimeJson {
  type Codec[A] = io.circe.Codec[A]
  val Codec: io.circe.Codec.type = io.circe.Codec

  type Encoder[A] = io.circe.Encoder[A]
  val Encoder: io.circe.Encoder.type = io.circe.Encoder

  type Decoder[A] = io.circe.Decoder[A]
  val Decoder: io.circe.Decoder.type = io.circe.Decoder

  object derive {
    import io.circe.generic.codec.DerivedAsObjectCodec
    import shapeless.Lazy

    def codec[T](implicit codec: Lazy[DerivedAsObjectCodec[T]]): Codec[T] =
      io.circe.generic.semiauto.deriveCodec[T]
  }

  implicit def sproutJSONEncoder[O, N](implicit ot: OldType[O, N], enc: Encoder[O]): Encoder[N] =
    enc.contramap(ot.oldType)

  implicit def sproutNewtypeJSONDecoder[O, N](implicit nt: NewType[O, N], dec: Decoder[O]): Decoder[N] =
    dec.imap(nt.newType)(nt.oldType)

  implicit def sproutRefinedThrowJSONDecoder[O, N](implicit nt: RefinedTypeThrow[O, N], dec: Decoder[O]): Decoder[N] =
    dec.emapTry(o => nt.newType[Try](o))

}
