package pms.service.user.rest

import org.http4s._
import pms.effects._
import pms.effects.implicits._
import pms.algebra.user._
import pms.algebra.http._
import pms.core.Module
import pms.service.user._

/**
  *
  * @author Lorand Szakacs, https://github.com/lorandszakacs
  * @since 27 Jun 2018
  *
  */
trait ModuleUserRest[F[_]] {
  this: Module[F] with ModuleUserService[F] with ModuleUserAlgebra[F] =>

  def userRestService: F[UserRoutes[F]] = _userRoutes

  def userLoginRoutes: F[UserLoginRoutes[F]] = _userLoginRoutes

  def userAccountRoutes: F[UserAccountRoutes[F]] = _userAccountRoutes

  def userModuleRoutes: F[HttpRoutes[F]] = _routes

  def userModuleAuthedRoutes: F[AuthCtxRoutes[F]] = _authedRoutes

  private lazy val _userRoutes: F[UserRoutes[F]] = singleton {
    userAlgebra.map(ua => new UserRoutes[F](userAlgebra = ua))
  }

  private lazy val _userLoginRoutes: F[UserLoginRoutes[F]] = singleton {
    userAuthAlgebra.map(uaa => new UserLoginRoutes[F](userAuthAlgebra = uaa))
  }

  private lazy val _userAccountRoutes: F[UserAccountRoutes[F]] = singleton {
    userAccountService.map(aac => new UserAccountRoutes(userService = aac))
  }

  private lazy val _routes: F[HttpRoutes[F]] = singleton {
    for {
      uar <- userAccountRoutes
      ulr <- userLoginRoutes
    } yield NonEmptyList.of(uar.routes, ulr.routes).reduceK
  }

  private lazy val _authedRoutes: F[AuthCtxRoutes[F]] = singleton {
    for {
      urs <- userRestService
      uar <- userAccountRoutes
    } yield NonEmptyList.of(urs.authedRoutes, uar.authedRoutes).reduceK
  }
}
