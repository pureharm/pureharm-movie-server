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
import phms.json.*
import phms.organizer.user.*

/** @author Lorand Szakacs, https://github.com/lorandszakacs
  * @since 26 Jun 2018
  */
trait UserRoutesJSON {

  implicit val userRoleCirceCodec: Codec[UserRole] = Codec.from(
    Decoder[String].emapTry(s => UserRole.fromName[Try](s)),
    Encoder[String].contramap(_.toName),
  )

  implicit val userInvitationCirceCodec: Codec[UserInvitation] =
    derive.codec[UserInvitation]

  implicit val userConfirmationCirceCodec: Codec[UserConfirmation] =
    derive.codec[UserConfirmation]

  implicit val userCirceCodec: Codec[User] = derive.codec[User]

  implicit val pwResetReqCirceCodec: Codec[PasswordResetRequest] =
    derive.codec[PasswordResetRequest]

  implicit val pwResetComCirceCodec: Codec[PasswordResetCompletion] =
    derive.codec[PasswordResetCompletion]

  implicit val authCtxCirceCodec: Codec[AuthCtx] = derive.codec[AuthCtx]

}
