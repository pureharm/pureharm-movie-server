package pms.service.user

import pms.algebra.user._
import pms.core.Module
import pms.email._

/**
  *
  * @author Lorand Szakacs, https://github.com/lorandszakacs
  * @since 27 Jun 2018
  *
  */
trait ModuleUserService[F[_]] {
  this: Module[F] with ModuleUserAlgebra[F] with ModuleEmail[F] =>

  def userAccountService: F[UserAccountService[F]] = _userService

  private lazy val _userService: F[UserAccountService[F]] = singleton {
    import pms.effects.implicits._
    for {
      uaa <- userAuthAlgebra
      uacc <- userAccountAlgebra
      ua <- userAlgebra
      ea <- emailAlgebra
    } yield
      UserAccountService.concurrent[F](
        userAuth = uaa,
        userAccount = uacc,
        userAlgebra = ua,
        emailAlgebra = ea,
      )
  }

}
