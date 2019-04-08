package pms.server

import cats.implicits._
import cats.effect.Timer
import doobie.util.transactor.Transactor
import org.http4s._

import pms.core.Module
import pms.effects._
import pms.email._
import pms.algebra.user._
import pms.algebra.imdb._
import pms.algebra.movie._
import pms.algebra.http._
import pms.service.user._
import pms.service.user.rest._
import pms.service.movie._
import pms.service.movie.rest._

/**
  * Overriding all abstract things just to make clear what
  * still needs to be provided for this whole thing to function
  *
  * @author Lorand Szakacs, https://github.com/lorandszakacs
  * @since 27 Jun 2018
  *
  */
trait ModulePureMovieServer[F[_]]
    extends Module[F] with ModuleEmail[F] with ModuleUserAlgebra[F] with ModuleIMDBAlgebra[F] with ModuleMovieAlgebra[F]
    with ModuleUserService[F] with ModuleMovieService[F] with ModuleUserRest[F] with ModuleMovieRest[F] {

  implicit override def F: Concurrent[F]

  override def gmailConfig: GmailConfig

  override def imdbAlgebraConfig: IMDBAlgebraConfig

  def pureMovieServerRoutes: F[HttpRoutes[F]] = _pureMovieServerRoutes

  //we could delay this even more, but there is little point.
  private lazy val authCtxMiddleware: F[AuthCtxMiddleware[F]] = singleton {
    userAuthAlgebra.map(uaa => AuthedHttp4s.userTokenAuthMiddleware[F](uaa))
  }

  private lazy val _pureMovieServerRoutes: F[HttpRoutes[F]] = singleton {
    import cats.implicits._
    for {
      umr <- userModuleRoutes
      routes = NonEmptyList.of[HttpRoutes[F]](umr).reduceK

      mmas <- movieModuleAuthedRoutes
      umar <- userModuleAuthedRoutes
      authed = NonEmptyList.of[AuthCtxRoutes[F]](umar, mmas).reduceK

      middleware <- authCtxMiddleware
    } yield {
      /*_*/
      routes <+> middleware(authed)
      /*_*/
    }

  }
}

object ModulePureMovieServer {

  def concurrent[F[_]](gConfig: GmailConfig, imbdAlgebraConfig: IMDBAlgebraConfig)(
    implicit
    c:  Concurrent[F],
    t:  Transactor[F],
    ti: Timer[F],
  ): F[ModulePureMovieServer[F]] = c.delay {
    new ModulePureMovieServer[F] {
      override def F: Concurrent[F] = c

      implicit override def timer: Timer[F] = ti

      override def gmailConfig: GmailConfig = gConfig

      override def imdbAlgebraConfig: IMDBAlgebraConfig = imbdAlgebraConfig

      implicit override def transactor: Transactor[F] = t

    }
  }

}
