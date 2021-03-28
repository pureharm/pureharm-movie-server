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

package phms.api.user

import phms.*
import phms.algebra.user.*
import phms.json.{*, given}
import phms.organizer.user.*

/** @author Lorand Szakacs, https://github.com/lorandszakacs
  * @since 26 Jun 2018
  */
trait UserRoutesJSON {

  given userRoleCirceCodec: Codec[UserRole] = Codec.from(
    Decoder[String].emapTry(s => UserRole.fromName[Try](s)),
    Encoder[String].contramap(_.toName),
  )

  given userInvitationCirceCodec: Codec[UserInvitation] =
    derive.codec[UserInvitation]

  given userConfirmationCirceCodec: Codec[UserConfirmation] =
    derive.codec[UserConfirmation]

  given userCirceCodec: Codec[User] = derive.codec[User]

  given pwResetReqCirceCodec: Codec[PasswordResetRequest] =
    derive.codec[PasswordResetRequest]

  given pwResetComCirceCodec: Codec[PasswordResetCompletion] =
    derive.codec[PasswordResetCompletion]

  given authCtxCirceCodec: Codec[AuthCtx] = derive.codec[AuthCtx]

}
