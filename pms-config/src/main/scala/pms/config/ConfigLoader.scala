package pms.config

import pms.effects._
import pureconfig._
import pureconfig.error.ConfigReaderFailures

/**
  *
  * Important to note:
  * Given a case class:
  * {{{
  *   final case class PureMovieServerConfig(
  *     port: Int,
  *     host: String,
  *     apiRoot: String,
  *   )
  * }}}
  * the ``apiRoot`` field will be read as ``api-root`` from the file.
  * So camelCase gets converted into "-" case.
  *
  * @author Lorand Szakacs, https://github.com/lorandszakacs
  * @since 20 Jun 2018
  *
  */
trait ConfigLoader[Config] {

  def default[F[_]: Sync]: F[Config]

  def load[F[_]: Sync](implicit reader: Derivation[ConfigReader[Config]]): F[Config] = {
    suspendInF(pureconfig.loadConfig[Config])
  }

  def load[F[_]: Sync](namespace: String)(implicit reader: Derivation[ConfigReader[Config]]): F[Config] = {
    suspendInF(pureconfig.loadConfig[Config](namespace))
  }

  private def suspendInF[F[_]: Sync](thunk: => Either[ConfigReaderFailures, Config]): F[Config] = {
    Sync[F].flatMap(Sync[F].delay(thunk)) {
      case Left(err) => Sync[F].raiseError(ConfigReadingAnomalies(err))
      case Right(c)  => Sync[F].pure(c)
    }
  }
}
