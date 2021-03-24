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

package phms.server.config

import phms._
import phms.config._
import phms.algebra.imdb._
import phms.db.config._
import phms.port.email._
import phms.port.email.GmailConfig

final case class PHMSServerConfig(
  httpConfig:  HttpConfig,
  emailConfig: GmailConfig,
  imdbConfig:  IMDBAlgebraConfig,
  dbConfig:    DatabaseConfig,
  boostrap:    BootstrapServer,
)

object PHMSServerConfig {

  def resource[F[_]: Config]: Resource[F, PHMSServerConfig] =
    for {
      serverConfig      <- httpConfig.resource[F]
      gmailConfig       <- gmailConfig.resource[F]
      imdbAlgebraConfig <- imdbAlgebraConfig.resource[F]
      dbConfig          <- databaseConfig.resource[F]
      bootstrap         <- bootstrapConfig.resource[F]
    } yield PHMSServerConfig(
      serverConfig,
      gmailConfig,
      imdbAlgebraConfig,
      dbConfig,
      bootstrap,
    )
  import com.comcast.ip4s._

  private object bootstrapConfig extends ConfigLoader[BootstrapServer] {

    override def configValue: ConfigValue[Effect, BootstrapServer] =
      env(EnvVar.PHMS_APP_DEV_MODE_BOOTSTRAP).as[BootstrapServer].default(BootstrapServer.True)
  }

  private object httpConfig extends ConfigLoader[HttpConfig] {

    override val configValue: ConfigValue[Effect, HttpConfig] = (
      env(EnvVar.PHMS_SERVER_PORT).as[Port].default(port"13120"),
      env(EnvVar.PHMS_SERVER_HOST).as[Host].default(host"0.0.0.0"),
      env(EnvVar.PHMS_SERVER_API_ROOT).as[APIRoot].default(APIRoot("/phms/api")),
      env(EnvVar.PHMS_APP_DEV_MODE_BOOTSTRAP).as[Boolean].default(false),
    ).parMapN(HttpConfig.apply)

  }

  private object gmailConfig extends ConfigLoader[GmailConfig] {

    override val configValue: ConfigValue[Effect, GmailConfig] = (
      env(EnvVar.PHMS_EMAIL_FROM).as[EmailSender].default(EmailSender("email@gmailprovider.com")),
      env(EnvVar.PHMS_EMAIL_USER).as[EmailUser].default(EmailUser("email@gmailprovider.com")),
      env(EnvVar.PHMS_EMAIL_PASSWORD).as[EmailPassword].default(EmailPassword("DontPutPasswordsHereLol")),
      env(EnvVar.PHMS_EMAIL_HOST).as[SmtpHost].default(SmtpHost(host"smtp.gmail.com")),
      env(EnvVar.PHMS_EMAIL_PORT).as[SmtpPort].default(SmtpPort(port"587")),
      env(EnvVar.PHMS_EMAIL_AUTH).as[SmtpAuth].default(SmtpAuth.True),
      env(EnvVar.PHMS_EMAIL_START_TLS).as[SmtpStartTLS].default(SmtpStartTLS.True),
    ).parMapN(GmailConfig.apply)
  }
  import scala.concurrent.duration._

  private object imdbAlgebraConfig extends ConfigLoader[IMDBAlgebraConfig] {

    override def configValue: ConfigValue[Effect, IMDBAlgebraConfig] = default(
      IMDBAlgebraConfig(
        requestsInterval = 2.seconds,
        requestsNumber   = 2000L,
      )
    )
  }

  private object databaseConfig extends ConfigLoader[DatabaseConfig] {

    override def configValue: ConfigValue[Effect, DatabaseConfig] =
      (connectionConfig, flywayConfig).parMapN(DatabaseConfig.apply)

    private val DefaultSchema = SchemaName("phms")

    /** Default values are the same as our dev environment docker script found @
      * ./docker-postgresql.sh
      */
    private val connectionConfig: ConfigValue[Effect, DBConnectionConfig] = (
      env(EnvVar.PHMS_DB_HOST).as[DBHost].default(DBHost(host"localhost")),
      env(EnvVar.PHMS_DB_PORT).as[DBPort].default(DBPort(port"11312")),
      env(EnvVar.PHMS_DB_NAME).as[DatabaseName].default(DatabaseName("mymoviedatabase")),
      env(EnvVar.PHMS_DB_USERNAME).as[DBUsername].default(DBUsername("busyuser")),
      env(EnvVar.PHMS_DB_PASSWORD).as[DBPassword].default(DBPassword("qwerty")),
      env(EnvVar.PHMS_DB_SCHEMA).as[SchemaName].default(DefaultSchema),
    ).parMapN(DBConnectionConfig.apply)

    private val flywayConfig: ConfigValue[Effect, FlywayConfig] = {
      ( //we want flyway to deal w/ the same Schema as our database connection config
        env(EnvVar.PHMS_DB_SCHEMA).as[SchemaName].default(DefaultSchema),
        env(EnvVar.PHMS_DB_FLYWAY_CLEAN_ON_VALIDATION)
          .as[CleanOnValidationError]
          .default(CleanOnValidationError.True),
      ).parMapN { case (schemaName, cleanOnValidationError) =>
        FlywayConfig(
          schemas                = List(schemaName),
          cleanOnValidationError = cleanOnValidationError,
        )
      }

    }
  }

}
