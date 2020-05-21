package pms.config

import pms.effects._
import pureconfig._
import pureconfig.error.ConfigReaderFailures
import pureconfig.generic.ExportMacros

import scala.language.experimental.macros

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

  implicit def exportReader[A]: Exported[ConfigReader[A]] =
    macro ExportMacros.exportDerivedReader[A]
  implicit def exportedReader[A](implicit ex: Exported[ConfigReader[A]]): ConfigReader[A] = ex.instance

  def default[F[_]: Sync]: F[Config]

  def load[F[_]: Sync](implicit reader: ConfigReader[Config]): F[Config] =
    suspendInF(pureconfig.loadConfig[Config](Derivation.Successful(reader)))

  def load[F[_]: Sync](namespace: String)(implicit reader: ConfigReader[Config]): F[Config] =
    suspendInF(pureconfig.loadConfig[Config](namespace)(Derivation.Successful(reader)))

  private def suspendInF[F[_]: Sync](thunk: => Either[ConfigReaderFailures, Config]): F[Config] = {
    val F = Sync.apply[F]
    F.flatMap(F.delay(thunk)) {
      case Left(err) => F.raiseError(ConfigReadingAnomalies(err))
      case Right(c)  => F.pure(c)
    }
  }
}
