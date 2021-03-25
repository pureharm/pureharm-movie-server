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

package phms.port.email

import phms._
import phms.kernel._
import phms.logger._

/** This style of writing algebras (in layman terms: interface) is called
  * "final tagless".
  *
  * @author Lorand Szakacs, https://github.com/lorandszakacs
  * @since 05 Jun 2018
  */
trait EmailPort[F[_]] {

  /** @param onCompletion
    * defines what to do based on the outcome of the email
    * @return
    */
  def sendEmail(to: Email, subject: Subject, content: Content)(
    onCompletion:   PartialFunction[OutcomeBackground[F], F[Unit]]
  ): F[Background[F]]
}

object EmailPort {

  def resource[F[_]](
    config:        GmailConfig
  )(implicit sync: Sync[F], supervisor: Supervisor[F], logging: Logging[F]): Resource[F, EmailPort[F]] =
    impl.EmailPortJavaxMail.resource[F](config).widen

}
