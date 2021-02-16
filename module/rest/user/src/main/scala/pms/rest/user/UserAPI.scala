package pms.rest.user

import org.http4s.HttpRoutes
import pms.algebra.http.AuthCtxRoutes
import pms.algebra.user.{UserAlgebra, UserAuthAlgebra}
import pms.core._
import pms.service.user.UserAccountService

trait UserAPI[F[_]] {
  def authedRoutes: AuthCtxRoutes[F]
  def routes:       HttpRoutes[F]
}

object UserAPI {

  def resource[F[_]: Concurrent](
    userAlgebra:     UserAlgebra[F],
    userAuthAlgebra: UserAuthAlgebra[F],
    userService:     UserAccountService[F],
  ): Resource[F, UserAPI[F]] =
    for {
      userRoutes        <- Resource.pure[F, UserRoutes[F]](new UserRoutes[F](userAlgebra))
      userLoginRoutes   <- Resource.pure[F, UserLoginRoutes[F]](new UserLoginRoutes[F](userAuthAlgebra))
      userAccountRoutes <- Resource.pure[F, UserAccountRoutes[F]](new UserAccountRoutes[F](userService))
    } yield new UserAPI[F] {

      override def authedRoutes: AuthCtxRoutes[F] =
        NonEmptyList.of(userRoutes.authedRoutes, userAccountRoutes.authedRoutes).reduceK

      override def routes: HttpRoutes[F] = NonEmptyList.of(userAccountRoutes.routes, userLoginRoutes.routes).reduceK
    }

}
