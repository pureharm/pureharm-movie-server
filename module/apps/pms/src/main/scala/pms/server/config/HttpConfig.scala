package pms.server.config

import com.comcast.ip4s.{Host, Port}
import pms.config._
import pms._

/** @author Lorand Szakacs, https://github.com/lorandszakacs
  * @since 20 Jun 2018
  */
final case class HttpConfig(
  port:      Port,
  host:      Host,
  apiRoot:   String,
  bootstrap: Boolean,
)

object HttpConfig extends ConfigLoader[HttpConfig] {
  import ciris._

  implicit private val portDecoder: ConfigDecoder[String, Port] =
    ConfigDecoder[String, Int].mapOption("Port")(Port.fromInt)

  implicit private val hostDecoder: ConfigDecoder[String, Host] =
    ConfigDecoder[String, String].mapOption("Host")(Host.fromString)

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
