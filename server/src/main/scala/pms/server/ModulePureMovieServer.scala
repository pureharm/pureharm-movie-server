package pms.server

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

  //we could delay this even more, but there is little point.
  override def authCtxMiddleware: AuthCtxMiddleware[F] =
    AuthedHttp4s.userTokenAuthMiddleware[F](userAuthAlgebra)

  def pureMovieServerService: HttpService[F] = {
    import cats.implicits._
    NonEmptyList
      .of[HttpService[F]](
        userModuleService,
        movieModuleService
      )
      .reduceK
  }
}

object ModulePureMovieServer {

  def concurrent[F[_]](imbdAlgebraConfig : IMDBAlgebraConfig, gConfig: GmailConfig)(implicit c: Concurrent[F], t: Transactor[F]): ModulePureMovieServer[F] =
    new ModulePureMovieServer[F] {
      override implicit def concurrent: Concurrent[F] = c

      override implicit def scheduler: Scheduler  = monix.execution.Scheduler.Implicits.global

      override def imdbAlgebraConfig: IMDBAlgebraConfig = imbdAlgebraConfig

      override def gmailConfig: GmailConfig = gConfig

      override implicit def transactor: Transactor[F] = t
    }
}
