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

package phms.algebra.user.impl

import phms.*
import phms.algebra.user.*
import phms.db.*

object PSQLUserCodecs extends PSQLUserCodecs

trait PSQLUserCodecs {
  import db.codecs.*

  //all our tokens are base64 encodings of 64 bytes ~ 4*(64/3) bytes.
  val varchar96_token: Codec[String] = varchar(96)
  val uuid_user_id:    Codec[UserID] = uuid.sprout[UserID]

  val enum_user_role: Codec[UserRole] =
    `enum`[UserRole](
      encode = (s: UserRole) => s.toName,
      decode = (s: String) => UserRole.fromName[Try](s).toOption,
      tpe    = skunk.data.Type("user_role"),
    )
}
