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

  import com.comcast.ip4s.{Host, Port}

  implicit private val portDecoder: ConfigDecoder[String, Port] =
    ConfigDecoder[String, Int].mapOption("Port")(Port.fromInt)

  implicit private val hostDecoder: ConfigDecoder[String, Host] =
    ConfigDecoder[String, String].mapOption("Host")(Host.fromString)

  private object httpConfig extends ConfigLoader[HttpConfig] {

    private val DefaultPort = Port.fromInt(21312).liftTo[Try](Fail.init(EnvVars.PMS_SERVER_PORT.show)).get
    private val DefaultHost = Host.fromString("0.0.0.0").liftTo[Try](Fail.init(EnvVars.PMS_SERVER_HOST.show)).get

    override val configValue: ConfigValue[Effect, HttpConfig] = (
      env(EnvVars.PMS_SERVER_PORT.show).as[Port].default(DefaultPort),
      env(EnvVars.PMS_SERVER_HOST.show).as[Host].default(DefaultHost),
      default(APIRoot("/pms/api")),
      env(EnvVars.PMS_APP_DEV_MODE_BOOTSTRAP.show).as[Boolean].default(false),
    ).parMapN(HttpConfig.apply)

  }

  private object gmailConfig extends ConfigLoader[GmailConfig] {

    private val DefaultEmailPort = Port.fromInt(587).liftTo[Try](Fail.init(EnvVars.PMS_EMAIL_PORT.show)).get

    private val DefaultEmailHost =
      Host.fromString("smtp.gmail.com").liftTo[Try](Fail.init(EnvVars.PMS_EMAIL_HOST.show)).get

    override val configValue: ConfigValue[Effect, GmailConfig] = (
      env(EnvVars.PMS_EMAIL_FROM.show).as[EmailSender].default("email@gmailprovider.com"),
      env(EnvVars.PMS_EMAIL_USER.show).as[EmailUser].default("email@gmailprovider.com"),
      env(EnvVars.PMS_EMAIL_PASSWORD.show).as[EmailPassword].default("DontPutPasswordsHereLol"),
      env(EnvVars.PMS_EMAIL_HOST.show).as[Host].default(DefaultEmailHost).map(_.toString),
      env(EnvVars.PMS_EMAIL_PORT.show).as[Port].default(DefaultEmailPort).map(_.value),
      env(EnvVars.PMS_EMAIL_AUTH.show).as[Boolean].default(true),
      env(EnvVars.PMS_EMAIL_START_TLS.show).as[Boolean].default(true),
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
      env(EnvVars.PMS_DB_HOST.show).as[DBHost].default(DBHost("localhost")),
      env(EnvVars.PMS_DB_PORT.show).as[DBPort].default(DBPort(5432)),
      env(EnvVars.PMS_DB_NAME.show).as[DatabaseName].default(DatabaseName("mymoviedatabase")),
      env(EnvVars.PMS_DB_USERNAME.show).as[DBUsername].default(DBUsername("busyuser")),
      env(EnvVars.PMS_DB_PASSWORD.show).as[DBPassword].default(DBPassword("qwerty")),
      env(EnvVars.PMS_DB_SCHEMA.show).as[SchemaName].default(DefaultSchema),
    ).parMapN(DBConnectionConfig.apply)

    private val flywayConfig: ConfigValue[Effect, FlywayConfig] = {
      ( //we want flyway to deal w/ the same Schema as our database connection config
        env(EnvVars.PMS_DB_SCHEMA.show).as[SchemaName].default(DefaultSchema),
        env(EnvVars.PMS_DB_FLYWAY_CLEAN_ON_VALIDATION.show)
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
