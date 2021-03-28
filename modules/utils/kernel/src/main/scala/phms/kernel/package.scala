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

package object kernel {

  type Email = Email.Type

  object Email extends SproutRefinedThrow[String] {

    override def refine[F[_]](o: String)(using F: MonadThrow[F]): F[String] =
      if (!o.contains("@")) Fail.invalid("Email must contain: @").raiseError[F, String] else o.pure[F]
  }

  type PlainTextPassword = PlainTextPassword.Type

  object PlainTextPassword extends SproutRefinedThrow[String] {

    /** TODO: make these rules configurable
      */
    override def refine[F[_]](o: String)(using F: MonadThrow[F]): F[String] =
      if (o.length < 6) Fail.invalid("Password needs to have at least 6 characters").raiseError[F, String]
      else if (o.getBytes(java.nio.charset.StandardCharsets.UTF_8).length > 72)
        Fail.invalid("Password needs to be less than 71 bytes long, i.e. ~ 35 chars long").raiseError[F, String]
      else
        o.pure[F]

    extension (p: PlainTextPassword) {
      def utf8Bytes: Array[Byte] = oldType(p).getBytes(java.nio.charset.StandardCharsets.UTF_8)
    }
  }
}
