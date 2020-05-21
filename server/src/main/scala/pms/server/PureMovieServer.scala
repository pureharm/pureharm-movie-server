package pms.server

import pms.effects._
import pms.effects.implicits._
import pms.logger._
import pms.email._
import pms.db.config._
import pms.algebra.imdb.IMDBAlgebraConfig

import doobie.util.transactor.Transactor

/**
  *
  * @author Lorand Szakacs, https://github.com/lorandszakacs
  * @since 11 Jul 2018
  *
  */
final class PureMovieServer[F[_]] private (
  private val timer:          Timer[F],
  private val dbContextShift: ContextShift[F],
)(implicit
  private val F:              Concurrent[F]
) {
  private val logger: PMSLogger[F] = PMSLogger.getLogger[F]

  def init: F[(PureMovieServerConfig, ModulePureMovieServer[F])] =
    for {
      serverConfig      <- PureMovieServerConfig.default[F]
      gmailConfig       <- GmailConfig.default[F]
      imdbAlgebraConfig <- IMDBAlgebraConfig.default[F]
      dbConfig          <- DatabaseConfig.default[F]
      transactor        <- DatabaseConfigAlgebra.transactor[F](dbConfig)(F, dbContextShift)
      nrOfMigs          <- DatabaseConfigAlgebra.initializeSQLDb[F](dbConfig)
      _                 <- logger.info(s"Successfully ran #$nrOfMigs migrations")
      pmsModule         <- moduleInit(
        gmailConfig,
        imdbAlgebraConfig,
        bootstrap = serverConfig.bootstrap,
      )(transactor, timer)
      _                 <- logger.info(s"Successfully initialized pure-movie-server")
    } yield (serverConfig, pmsModule)

  private def moduleInit(
    gmailConfig:      GmailConfig,
    imdblgebraConfig: IMDBAlgebraConfig,
    bootstrap:        Boolean,
  )(implicit
    transactor:       Transactor[F],
    timer:            Timer[F],
  ): F[ModulePureMovieServer[F]] =
    if (bootstrap) {
      logger.warn(
        "BOOTSTRAP — initializing server in bootstrap mode — if this is on prod, you seriously botched this one"
      ) >> ModulePureMovieServerBootstrap
        .concurrent(gmailConfig, imdblgebraConfig)
        .flatTap(_.bootstrap)
        .widen[ModulePureMovieServer[F]]
    }
    else ModulePureMovieServer.concurrent(gmailConfig, imdblgebraConfig)

}

object PureMovieServer {

  def concurrent[F[_]: Concurrent](timer: Timer[F], dbContextShift: ContextShift[F]): F[PureMovieServer[F]] =
    Concurrent.apply[F].delay(new PureMovieServer[F](timer, dbContextShift))
}
