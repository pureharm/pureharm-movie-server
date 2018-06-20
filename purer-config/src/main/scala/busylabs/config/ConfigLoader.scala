package busylabs.config

import cats.effect.Sync
import pureconfig._
import pureconfig.error.ConfigReaderFailures

/**
  *
  * @author Lorand Szakacs, https://github.com/lorandszakacs
  * @since 20 Jun 2018
  *
  */
trait ConfigLoader[Config] {

  def load[F[_]: Sync](implicit reader: Derivation[ConfigReader[Config]]): F[Config] = {
    suspendInF(pureconfig.loadConfig[Config])
  }

  private def suspendInF[F[_]: Sync](thunk: => Either[ConfigReaderFailures, Config]): F[Config] = {
    Sync[F].flatMap(Sync[F].delay(thunk)) {
      case Left(err) => Sync[F].raiseError(ConfigReadingAnomalies(err))
      case Right(c) => Sync[F].pure(c)
    }
  }
}
