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

package phms.db

import phms._

object codecs extends skunk.codec.AllCodecs with KernelSkunkCodecs {

  implicit class Ops[O](val c: skunk.Codec[O]) extends AnyVal {

    def sprout[N](implicit s: NewType[O, N]): skunk.Codec[N] =
      c.imap(s.newType)(s.oldType)

    def sproutRefined[N](implicit s: RefinedTypeThrow[O, N]): skunk.Codec[N] =
      skunk.Codec.from[N](
        enc = c.contramap(s.oldType),
        dec = c.emap(v => s.newType[Attempt](v).leftMap(_.toString)),
      )
  }

  implicit class CodecCompanionOps(val c: skunk.Codec.type) extends AnyVal {

    def from[A](enc: skunk.Encoder[A], dec: skunk.Decoder[A]): skunk.Codec[A] = {
      def initException = new java.lang.ExceptionInInitializerError(
        s"""
           |You tried creating a skunk.Codec from a
           |skunk.Encoder and skunk.Decoder that have different
           |type lists. These two lists have to match.
           |
           |This exception was thrown at initialization time.
           |
           |Check the definition of you Codec. If you still
           |think this is correct, then roll your own Codec.
           |
           |encoder.types.length = ${enc.types}
           |decoder.types.length = ${enc.types}
           |
           |encoder.types = ${enc.types.mkString(",")}
           |decoder.types = ${dec.types.mkString(",")}
           |""".stripMargin
      )
      if (enc.types.length != dec.types.length)
        throw initException
      else {
        val encSet = enc.types.toSet
        val deCSet = dec.types.toSet
        if (encSet != deCSet)
          throw initException
      }
      new skunk.Codec[A] {
        override def types: List[skunk.data.Type] = enc.types

        override def decode(offset: Int, ss: List[Option[String]]): Either[skunk.Decoder.Error, A] =
          dec.decode(offset, ss)

        override def sql: cats.data.State[Int, String] = enc.sql

        override def encode(a: A): List[Option[String]] = enc.encode(a)
      }
    }
  }
}
