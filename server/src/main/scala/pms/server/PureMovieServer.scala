package pms.server

import cats.implicits._

import pms.effects._
import pms.email._
import pms.db.config._

import pms.algebra.imdb.IMDBAlgebraConfig

import io.chrisdavenport.log4cats.slf4j.Slf4jLogger
import doobie.util.transactor.Transactor

/**
  *
  * @author Lorand Szakacs, https://github.com/lorandszakacs
  * @since 11 Jul 2018
  *
  */
final class PureMovieServer[F[_]] private (
  implicit private val F: Concurrent[F],
  private val scheduler:  Scheduler
) {
  private val logger = Slf4jLogger.unsafeCreate[F]

  def init: F[(PureMovieServerConfig, ModulePureMovieServer[F])] = {
    for {
      serverConfig      <- PureMovieServerConfig.default[F]
      gmailConfig       <- GmailConfig.default[F]
      imdbAlgebraConfig <- IMDBAlgebraConfig.default[F]
      dbConfig          <- DatabaseConfig.default[F]
      transactor        <- DatabaseConfigAlgebra.transactor[F](dbConfig)
      nrOfMigs          <- DatabaseConfigAlgebra.initializeSQLDb[F](dbConfig)
      _                 <- logger.info(s"Successfully ran #$nrOfMigs migrations")
      pmsModule <- moduleInit(
                    gmailConfig,
                    imdbAlgebraConfig,
                    bootstrap = serverConfig.bootstrap
                  )(transactor, implicitly[Scheduler])
      _ <- logger.info(s"Successfully initialized pure-movie-server")
    } yield (serverConfig, pmsModule)
  }

  private def moduleInit(
    gmailConfig:      GmailConfig,
    imdblgebraConfig: IMDBAlgebraConfig,
    bootstrap:        Boolean
  )(
    implicit
    transactor: Transactor[F],
    scheduler:  Scheduler
  ): F[ModulePureMovieServer[F]] = {
    if (bootstrap) {
      logger.warn(
        "BOOTSTRAP — initializing server in bootstrap mode — if this is on prod, you seriously botched this one"
      ) *>
        F.delay(ModulePureMovieServerBootstrap.concurrent(gmailConfig, imdblgebraConfig)).flatMap { module =>
          module.bootstrap >> F.pure(module)
        }
    }
    else {
      F.delay(ModulePureMovieServer.concurrent(gmailConfig, imdblgebraConfig))
    }

  }

}

object PureMovieServer {

  def concurrent[F[_]: Concurrent](implicit sch: Scheduler): F[PureMovieServer[F]] =
    Concurrent.apply[F].delay(new PureMovieServer[F]())
}
