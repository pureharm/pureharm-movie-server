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

  def userRestService: UserRestService[F] = _userRestService

  def userLoginRestService: UserLoginRestService[F] = _userLoginRestService

  def userAccountRestService: UserAccountRestService[F] = _userAccountRestService

  def userModuleService: HttpService[F] = _service

  def userModuleAuthedService: AuthCtxService[F] = _authedService

  private lazy val _userRestService: UserRestService[F] = new UserRestService[F](
    userAlgebra = userAlgebra
  )

  private lazy val _userLoginRestService: UserLoginRestService[F] = new UserLoginRestService[F](
    userAuthAlgebra = userAuthAlgebra
  )

  private lazy val _userAccountRestService: UserAccountRestService[F] = new UserAccountRestService(
    userService = userAccountService
  )

  import cats.implicits._

  private lazy val _service: HttpService[F] =
    NonEmptyList
      .of(
        userAccountRestService.service,
        userLoginRestService.service
      )
      .reduceK

  private lazy val _authedService: AuthCtxService[F] =
    NonEmptyList
      .of(
        userRestService.authedService,
        userAccountRestService.authedService,
      )
      .reduceK
}
