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

  def authCtxMiddleware: AuthCtxMiddleware[F]

  def userRestService: UserRestService[F] = _userRestService

  def userAuthRestService: UserAuthRestService[F] = _userAuthRestService

  def userAccountRestService: UserAccountRestService[F] = _userAccountRestService

  def userModuleService: HttpService[F] = _service

  private lazy val _userRestService: UserRestService[F] = new UserRestService[F](
    userAlgebra = userAlgebra,
    authCtxMiddleware,
  )

  private lazy val _userAuthRestService: UserAuthRestService[F] = new UserAuthRestService[F](
    userAuthAlgebra = userAuthAlgebra
  )

  private lazy val _userAccountRestService: UserAccountRestService[F] = new UserAccountRestService(
    userService = userAccountService,
    authCtxMiddleware,
  )

  private lazy val _service: HttpService[F] = {
    import cats.implicits._
    NonEmptyList
      .of(
        userAuthRestService.service,
        userRestService.service,
        userAccountRestService.service
      )
      .reduceK
  }
}
