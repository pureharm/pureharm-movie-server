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

  def resource[F[_]: Monad: Config]: Resource[F, PMSServerConfig] =
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

  import ciris._
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
      env(EnvVars.PMS_APP_DEV_MODE_BOOTSTRAP.show).as[Boolean].default(false),
    ).parMapN { case (port, host, bootstrap) =>
      HttpConfig(
        port      = port,
        host      = host,
        apiRoot   = "/pms/api/",
        bootstrap = bootstrap,
      )
    }
  }

  private object gmailConfig extends ConfigLoader[GmailConfig] {
    
    private val DefaultEmailPort = Port.fromInt(587).liftTo[Try](Fail.init(EnvVars.PMS_EMAIL_PORT.show)).get

    private val DefaultEmailHost =
      Host.fromString("smtp.gmail.com").liftTo[Try](Fail.init(EnvVars.PMS_EMAIL_HOST.show)).get

    override val configValue: ConfigValue[Effect, GmailConfig] = (
      env(EnvVars.PMS_EMAIL_FROM.show).as[EmailSender].default("email@gmailprovider.com"),
      env(EnvVars.PMS_EMAIL_USER.show).as[EmailUser].default("email@gmailprovider.com"),
      env(EnvVars.PMS_EMAIL_PASSWORD.show).as[EmailPassword].default("DontPutPasswordsHereLol"),
      env(EnvVars.PMS_EMAIL_HOST.show).as[Host].default(DefaultEmailHost),
      env(EnvVars.PMS_EMAIL_PORT.show).as[Port].default(DefaultEmailPort),
      env(EnvVars.PMS_EMAIL_AUTH.show).as[Boolean].default(true),
      env(EnvVars.PMS_EMAIL_START_TLS.show).as[Boolean].default(true),
    ).parMapN { case (sender, user, pwd, host, port, auth, startTLS) =>
      GmailConfig(
        from     = sender,
        user     = user,
        password = pwd,
        host     = host.toString,
        port     = port.value,
        auth     = auth,
        startTLS = startTLS,
      )
    }
  }

  private object imdbAlgebraConfig extends ConfigLoader[IMDBAlgebraConfig] {
    override def configValue: ConfigValue[Effect, IMDBAlgebraConfig] = ???
  }

  private object databaseConfig extends ConfigLoader[DatabaseConfig] {
    override def configValue: ConfigValue[Effect, DatabaseConfig] = ???
  }

}
