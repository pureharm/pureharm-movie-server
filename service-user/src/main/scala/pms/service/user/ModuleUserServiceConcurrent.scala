package pms.service.user

import pms.effects._

import pms.algebra.user._
import pms.email._

/**
  *
  * @author Lorand Szakacs, https://github.com/lorandszakacs
  * @since 27 Jun 2018
  *
  */
trait ModuleUserServiceConcurrent[F[_]] { this: ModuleUserAsync[F] with ModuleEmailASync[F] =>

  implicit def concurrent: Concurrent[F]

  def userAccountService: UserAccountService[F] = _userService

  private lazy val _userService: UserAccountService[F] =
    UserAccountService.concurrent[F](
      userAuth     = userAuthAlgebra,
      userAccount  = userAccountAlgebra,
      userAlgebra  = userAlgebra,
      emailAlgebra = emailAlgebra,
    )

}
