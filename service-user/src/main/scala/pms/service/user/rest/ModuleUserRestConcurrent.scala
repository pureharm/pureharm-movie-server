package pms.service.user.rest

import org.http4s._

import pms.effects._

import pms.algebra.user._
import pms.algebra.http._
import pms.service.user._

/**
  *
  * @author Lorand Szakacs, https://github.com/lorandszakacs
  * @since 27 Jun 2018
  *
  */
trait ModuleUserRestConcurrent[F[_]] { this: ModuleUserServiceConcurrent[F] with ModuleUserAsync[F] =>

  def userRestService: UserRoutes[F] = _userRoutes

  def userLoginRoutes: UserLoginRoutes[F] = _userLoginRoutes

  def userAccountRoutes: UserAccountRoutes[F] = _userAccountRoutes

  def userModuleRoutes: HttpRoutes[F] = _routes

  def userModuleAuthedRoutes: AuthCtxRoutes[F] = _authedRoutes

  private lazy val _userRoutes: UserRoutes[F] = new UserRoutes[F](
    userAlgebra = userAlgebra
  )

  private lazy val _userLoginRoutes: UserLoginRoutes[F] = new UserLoginRoutes[F](
    userAuthAlgebra = userAuthAlgebra
  )

  private lazy val _userAccountRoutes: UserAccountRoutes[F] = new UserAccountRoutes(
    userService = userAccountService
  )

  import cats.implicits._

  private lazy val _routes: HttpRoutes[F] =
    NonEmptyList
      .of(
        userAccountRoutes.routes,
        userLoginRoutes.routes
      )
      .reduceK

  private lazy val _authedRoutes: AuthCtxRoutes[F] =
    NonEmptyList
      .of(
        userRestService.authedRoutes,
        userAccountRoutes.authedRoutes,
      )
      .reduceK
}
