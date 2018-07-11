package pms.server

import cats.implicits._

import pms.effects._
import pms.email._
import pms.db.config._

import io.chrisdavenport.log4cats.slf4j.Slf4jLogger
import doobie.util.transactor.Transactor

/**
  *
  * @author Lorand Szakacs, https://github.com/lorandszakacs
  * @since 11 Jul 2018
  *
  */
final class PureMovieServer[F[_]: Concurrent] private () {

  private val logger = Slf4jLogger.unsafeCreate[F]

  def init: F[(PureMovieServerConfig, ModulePureMovieServer[F])] = {
    for {
      serverConfig <- PureMovieServerConfig.default[F]
      gmailConfig  <- GmailConfig.default[F]
      dbConfig     <- DatabaseConfig.default[F]
      transactor   <- DatabaseConfigAlgebra.transactor[F](dbConfig)
      nrOfMigs     <- DatabaseConfigAlgebra.initializeSQLDb[F](dbConfig)
      _            <- logger.info(s"Successfully ran #$nrOfMigs migrations")
      pmsModule    <- moduleInit(gmailConfig, transactor)
      _            <- logger.info(s"Successfully initialized pure-movie-server")
    } yield (serverConfig, pmsModule)
  }

  private def moduleInit(
    gmailConfig: GmailConfig,
    transactor:  Transactor[F]
  ): F[ModulePureMovieServer[F]] =
    Concurrent.apply[F].delay(ModulePureMovieServer.concurrent(gmailConfig)(implicitly, transactor))

}

object PureMovieServer {
  def concurrent[F[_]: Concurrent]: F[PureMovieServer[F]] = Concurrent.apply[F].delay(new PureMovieServer[F]())
}
