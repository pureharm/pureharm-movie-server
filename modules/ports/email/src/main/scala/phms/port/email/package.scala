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

package phms.port

import phms.*
import com.comcast.ip4s.*

/** @author Lorand Szakacs, https://github.com/lorandszakacs
  * @since 05 Jun 2018
  */
package object email {

  type EmailSender = EmailSender.Type
  object EmailSender extends SproutSub[String]

  type EmailUser = EmailUser.Type
  object EmailUser extends SproutSub[String]

  type EmailPassword = EmailPassword.Type
  object EmailPassword extends SproutSub[String]

  type SmtpHost = SmtpHost.Type
  object SmtpHost extends SproutSub[Host]
  type SmtpPort = SmtpPort.Type
  object SmtpPort extends SproutSub[Port]

  type SmtpAuth = SmtpAuth.Type

  object SmtpAuth extends SproutSub[Boolean] {
    val False: SmtpAuth = newType(false)
    val True:  SmtpAuth = newType(true)
  }
  type SmtpStartTLS = SmtpStartTLS.Type

  object SmtpStartTLS extends SproutSub[Boolean] {
    val False: SmtpStartTLS = newType(false)
    val True:  SmtpStartTLS = newType(true)
  }

  type Subject = Subject.Type
  object Subject extends SproutSub[String]
  type Content = Content.Type
  object Content extends SproutSub[String]

}
