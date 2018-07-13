package pms.server

import pms.effects._
import pms.email._

import pms.algebra.user._
import pms.algebra.imdb.IMDBAlgebraConfig

import pms.server.bootstrap._

import doobie.util.transactor.Transactor

/**
  *
  * @author Lorand Szakacs, https://github.com/lorandszakacs
  * @since 13 Jul 2018
  *
  */
trait ModulePureMovieServerBootstrap[F[_]]
    extends ModulePureMovieServer[F] with ModuleServerBootstrap[F] with ModuleUserBootstrap[F] {

  def bootstrap: F[Unit] = Bootstrap.bootstrap(serverBootstrapAlgebra)
}

object ModulePureMovieServerBootstrap {

  def concurrent[F[_]](
    gConfig:           GmailConfig,
    imbdAlgebraConfig: IMDBAlgebraConfig
  )(
    implicit
    c:  Concurrent[F],
    t:  Transactor[F],
    sc: Scheduler
  ): ModulePureMovieServerBootstrap[F] =
    new ModulePureMovieServerBootstrap[F] {
      implicit override def concurrent: Concurrent[F] = c

      implicit override def scheduler: Scheduler = sc

      override def gmailConfig: GmailConfig = gConfig

      override def imdbAlgebraConfig: IMDBAlgebraConfig = imbdAlgebraConfig

      implicit override def transactor: Transactor[F] = t
    }
}
