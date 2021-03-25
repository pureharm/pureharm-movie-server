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

/** All environment variables that our app uses are kept here for easy lookup,
  * and hopefully for future dhall inter-op.
  */
sealed trait EnvVar extends Product with Serializable {
  def show: String = EnvVar.showEnvVars.show(this)
}

object EnvVar {
  implicit val showEnvVars: Show[EnvVar] = Show.show(_.productPrefix)

  case object PHMS_APP_DEV_MODE_BOOTSTRAP extends EnvVar

  case object PHMS_SERVER_PORT     extends EnvVar
  case object PHMS_SERVER_HOST     extends EnvVar
  case object PHMS_SERVER_API_ROOT extends EnvVar

  case object PHMS_DB_HOST     extends EnvVar
  case object PHMS_DB_PORT     extends EnvVar
  case object PHMS_DB_NAME     extends EnvVar
  case object PHMS_DB_USERNAME extends EnvVar
  case object PHMS_DB_PASSWORD extends EnvVar
  case object PHMS_DB_SCHEMA   extends EnvVar

  case object PHMS_DB_FLYWAY_CLEAN_ON_VALIDATION extends EnvVar

  case object PHMS_EMAIL_FROM      extends EnvVar
  case object PHMS_EMAIL_USER      extends EnvVar
  case object PHMS_EMAIL_PASSWORD  extends EnvVar
  case object PHMS_EMAIL_HOST      extends EnvVar
  case object PHMS_EMAIL_PORT      extends EnvVar
  case object PHMS_EMAIL_AUTH      extends EnvVar
  case object PHMS_EMAIL_START_TLS extends EnvVar

}
