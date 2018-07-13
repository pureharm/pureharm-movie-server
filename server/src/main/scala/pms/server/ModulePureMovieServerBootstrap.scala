package pms.server

import doobie.util.transactor.Transactor

import pms.effects._
import pms.email._
import pms.algebra.user._

import pms.server.bootstrap._

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

  def concurrent[F[_]](gConfig: GmailConfig)(implicit c: Concurrent[F], t: Transactor[F]): ModulePureMovieServerBootstrap[F] =
    new ModulePureMovieServerBootstrap[F] {
      implicit override def concurrent: Concurrent[F] = c

      override def gmailConfig: GmailConfig = gConfig

      implicit override def transactor: Transactor[F] = t
    }
}
