package pms.server

import cats.effect.Timer
import doobie.util.transactor.Transactor
import org.http4s._
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
import monix.execution.Scheduler

/**
  * Overriding all abstract things just to make clear what
  * still needs to be provided for this whole thing to function
  *
  * @author Lorand Szakacs, https://github.com/lorandszakacs
  * @since 27 Jun 2018
  *
  */
trait ModulePureMovieServer[F[_]]
    extends ModuleEmailASync[F] with ModuleUserAsync[F] with ModuleIMDBAsync[F] with ModuleMovieAsync[F]
    with ModuleUserServiceConcurrent[F] with ModuleUserRestConcurrent[F] with ModuleMovieServiceAsync[F]
    with ModuleMovieRestAsync[F] {

  implicit override def concurrent: Concurrent[F]
  //at this point we can use the same concurrent instance
  implicit override def async: Async[F] = concurrent

  override def gmailConfig: GmailConfig

  override def imdbAlgebraConfig: IMDBAlgebraConfig

  //we could delay this even more, but there is little point.
  def authCtxMiddleware: AuthCtxMiddleware[F] =
    AuthedHttp4s.userTokenAuthMiddleware[F](userAuthAlgebra)

  def pureMovieServerRoutes: F[HttpRoutes[F]] = {
    import cats.implicits._
    val routes = NonEmptyList
      .of[HttpRoutes[F]](
        userModuleRoutes
      )
      .reduceK

      for {
        mmas <- movieModuleAuthedRoutes
        authed = NonEmptyList
          .of[AuthCtxRoutes[F]](
            userModuleAuthedRoutes,
            mmas
          )
          .reduceK
      } yield {
        /*_*/
        routes <+> authCtxMiddleware(authed)
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
    sc: Scheduler
  ): ModulePureMovieServer[F] =
    new ModulePureMovieServer[F] {
      implicit override def concurrent: Concurrent[F] = c

      implicit override def timer: Timer[F] = ti

      override def gmailConfig: GmailConfig = gConfig

      override def imdbAlgebraConfig: IMDBAlgebraConfig = imbdAlgebraConfig

      implicit override def transactor: Transactor[F] = t
    }
}
