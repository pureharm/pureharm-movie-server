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

package object db extends skunk.syntax.ToAllOps {
  type Session[F[_]] = skunk.Session[F]
  val Session: skunk.Session.type = skunk.Session

  type DBPool[F[_]] = Resource[F, Session[F]]

  type Codec[A]   = skunk.Codec[A]
  type Encoder[A] = skunk.Encoder[A]
  type Decoder[A] = skunk.Decoder[A]

  val Codec:   skunk.Codec.type   = skunk.Codec
  val Encoder: skunk.Encoder.type = skunk.Encoder
  val Decoder: skunk.Decoder.type = skunk.Decoder

  type Void = skunk.Void
  val Void: skunk.Void.type = skunk.Void

  type Command[A]  = skunk.Command[A]
  type Query[I, O] = skunk.Query[I, O]

  type ~[+A, +B] = skunk.~[A, B]

  type PreparedQuery[F[_], I, O] = skunk.PreparedQuery[F, I, O]
  val PreparedQuery: skunk.PreparedQuery.type = skunk.PreparedQuery

  type PreparedCommand[F[_], A] = skunk.PreparedCommand[F, A]
  val PreparedCommand: skunk.PreparedCommand.type = skunk.PreparedCommand

  type Column    = skunk.Fragment[skunk.Void]
  type TableName = skunk.Fragment[skunk.Void]
  type Row       = skunk.Fragment[skunk.Void]
}
