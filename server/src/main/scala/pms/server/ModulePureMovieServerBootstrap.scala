package pms.server

import cats.effect.Timer
import pms.effects._
import pms.email._
import pms.algebra.user._
import pms.algebra.imdb.IMDBAlgebraConfig
import pms.server.bootstrap._
import doobie.util.transactor.Transactor
import pms.core.Module
import cats.implicits._

/**
  *
  * @author Lorand Szakacs, https://github.com/lorandszakacs
  * @since 13 Jul 2018
  *
  */
trait ModulePureMovieServerBootstrap[F[_]]
    extends Module[F] with ModulePureMovieServer[F] with ModuleServerBootstrap[F] with ModuleUserBootstrap[F] {

  def bootstrap: F[Unit] = serverBootstrapAlgebra.flatMap(sba => Bootstrap.bootstrap(sba))
}

object ModulePureMovieServerBootstrap {

  def concurrent[F[_]](
    gConfig:           GmailConfig,
    imbdAlgebraConfig: IMDBAlgebraConfig
  )(
    implicit
    c:  Concurrent[F],
    t:  Transactor[F],
    ti: Timer[F],
  ): ModulePureMovieServerBootstrap[F] =
    new ModulePureMovieServerBootstrap[F] {
      implicit override def F: Concurrent[F] = c

      implicit override def timer: Timer[F] = ti

      override def gmailConfig: GmailConfig = gConfig

      override def imdbAlgebraConfig: IMDBAlgebraConfig = imbdAlgebraConfig

      implicit override def transactor: Transactor[F] = t

    }
}
