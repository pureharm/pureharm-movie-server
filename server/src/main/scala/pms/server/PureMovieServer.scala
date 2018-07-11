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
object PureMovieServer {

  def init[F[_]: Concurrent]: F[(PureMovieServerConfig, ModulePureMovieServer[F])] = {
    for {
      logger       <- Slf4jLogger.create[F]
      serverConfig <- PureMovieServerConfig.default[F]
      gmailConfig  <- GmailConfig.default[F]
      dbConfig     <- DatabaseConfig.default[F]
      transactor   <- DatabaseConfigAlgebra.transactor[F](dbConfig)
      nrOfMigs     <- DatabaseConfigAlgebra.initializeSQLDb[F](dbConfig)
      _            <- logger.info(s"Successfully ran #$nrOfMigs migrations")
      pmsModule    <- moduleInit[F](gmailConfig, transactor)
      _            <- logger.info(s"Successfully initialized pure-movie-server")
    } yield (serverConfig, pmsModule)
  }

  private def moduleInit[F[_]: Concurrent](
    gmailConfig: GmailConfig,
    transactor:  Transactor[F]
  ): F[ModulePureMovieServer[F]] =
    Concurrent.apply[F].delay(ModulePureMovieServer.concurrent(gmailConfig)(implicitly, transactor))

}
