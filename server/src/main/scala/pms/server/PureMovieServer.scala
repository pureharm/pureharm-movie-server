package pms.server

import cats.effect.ContextShift
import doobie.util.transactor.Transactor
import org.http4s.HttpApp
import pms.algebra.imdb.IMDBAlgebraConfig
import pms.db.config._
import pms.effects._
import pms.effects.implicits._
import pms.email._
import pms.logger._
import pms.server.config.PureMovieServerConfig
import scala.concurrent.ExecutionContext

/**
  *
  * @author Lorand Szakacs, https://github.com/lorandszakacs
  * @since 11 Jul 2018
  *
  */
final class PureMovieServer[F[_]] private (logger: PMSLogger[F])(implicit private val F: Concurrent[F]) {

  def initialise(
    timer:              Timer[F],
    mainContextShift:   ContextShift[F],
    dbExecutionContext: ExecutionContext,
  ): Resource[F, (PureMovieServerConfig, HttpApp[F])] =
    for {
      serverConfig      <- PureMovieServerConfig.defaultR[F]
      gmailConfig       <- GmailConfig.defaultR[F]
      imdbAlgebraConfig <- IMDBAlgebraConfig.defaultR[F]
      dbConfig          <- DatabaseConfig.defaultR[F]

      transactor        <- DatabaseConfigAlgebra.transactor[F](dbExecutionContext, dbConfig.connection)(F, mainContextShift)
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
      //TODO: add better logic for handling boostrap being applied twice
      // important aspects to consider:
      //  1) give good diagnostics of what specifically went wrong so that the developer knows what's up
      //  2) distinguish between recoverable errors in bootstrap, and non-recoverable errors
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

  def resource[F[_]: Concurrent](logger: PMSLogger[F]): Resource[F, PureMovieServer[F]] =
    Resource.pure(new PureMovieServer[F](logger))

}
