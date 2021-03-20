package pms.server.config

import pms._
import pms.config._
import pms.algebra.imdb._
import pms.db.config._
import pms.email._

final case class PMSServerConfig(
  httpConfig:  HttpConfig,
  emailConfig: GmailConfig,
  imdbConfig:  IMDBAlgebraConfig,
  dbConfig:    DatabaseConfig,
)

object PMSServerConfig {

  def resource[F[_]: Config]: Resource[F, PMSServerConfig] =
    for {
      serverConfig      <- httpConfig.resource[F]
      gmailConfig       <- gmailConfig.resource[F]
      imdbAlgebraConfig <- imdbAlgebraConfig.resource[F]
      dbConfig          <- databaseConfig.resource[F]
    } yield PMSServerConfig(
      serverConfig,
      gmailConfig,
      imdbAlgebraConfig,
      dbConfig,
    )
  import com.comcast.ip4s._

  private object httpConfig extends ConfigLoader[HttpConfig] {

    override val configValue: ConfigValue[Effect, HttpConfig] = (
      env(EnvVar.PMS_SERVER_PORT).as[Port].default(port"21312"),
      env(EnvVar.PMS_SERVER_HOST).as[Host].default(host"0.0.0.0"),
      env(EnvVar.PMS_SERVER_API_ROOT).as[APIRoot].default(APIRoot("/pms/api")),
      env(EnvVar.PMS_APP_DEV_MODE_BOOTSTRAP).as[Boolean].default(false),
    ).parMapN(HttpConfig.apply)

  }

  private object gmailConfig extends ConfigLoader[GmailConfig] {

    override val configValue: ConfigValue[Effect, GmailConfig] = (
      env(EnvVar.PMS_EMAIL_FROM).as[EmailSender].default(EmailSender("email@gmailprovider.com")),
      env(EnvVar.PMS_EMAIL_USER).as[EmailUser].default(EmailUser("email@gmailprovider.com")),
      env(EnvVar.PMS_EMAIL_PASSWORD).as[EmailPassword].default(EmailPassword("DontPutPasswordsHereLol")),
      env(EnvVar.PMS_EMAIL_HOST).as[SmtpHost].default(SmtpHost(host"smtp.gmail.com")),
      env(EnvVar.PMS_EMAIL_PORT).as[SmtpPort].default(SmtpPort(port"587")),
      env(EnvVar.PMS_EMAIL_AUTH).as[SmtpAuth].default(SmtpAuth.True),
      env(EnvVar.PMS_EMAIL_START_TLS).as[SmtpStartTLS].default(SmtpStartTLS.True),
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

    private val DefaultSchema = SchemaName("pms")

    /** Default values are the same as our dev environment docker script found @
      * ./docker-postgresql.sh
      */
    private val connectionConfig: ConfigValue[Effect, DBConnectionConfig] = (
      env(EnvVar.PMS_DB_HOST).as[DBHost].default(DBHost(host"localhost")),
      env(EnvVar.PMS_DB_PORT).as[DBPort].default(DBPort(port"5432")),
      env(EnvVar.PMS_DB_NAME).as[DatabaseName].default(DatabaseName("mymoviedatabase")),
      env(EnvVar.PMS_DB_USERNAME).as[DBUsername].default(DBUsername("busyuser")),
      env(EnvVar.PMS_DB_PASSWORD).as[DBPassword].default(DBPassword("qwerty")),
      env(EnvVar.PMS_DB_SCHEMA).as[SchemaName].default(DefaultSchema),
    ).parMapN(DBConnectionConfig.apply)

    private val flywayConfig: ConfigValue[Effect, FlywayConfig] = {
      ( //we want flyway to deal w/ the same Schema as our database connection config
        env(EnvVar.PMS_DB_SCHEMA).as[SchemaName].default(DefaultSchema),
        env(EnvVar.PMS_DB_FLYWAY_CLEAN_ON_VALIDATION)
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
