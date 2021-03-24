package phms.rest.user

import org.http4s.HttpRoutes
import phms.stack.http.AuthCtxRoutes
import phms.algebra.user.{UserAlgebra, UserAuthAlgebra}
import phms._
import phms.organizer.user.UserAccountOrganizer

trait UserAPI[F[_]] {
  def authedRoutes: AuthCtxRoutes[F]
  def routes:       HttpRoutes[F]
}

object UserAPI {

  def resource[F[_]: Async](
    userAlgebra:          UserAlgebra[F],
    userAuthAlgebra:      UserAuthAlgebra[F],
    userAccountOrganizer: UserAccountOrganizer[F],
  ): Resource[F, UserAPI[F]] =
    for {
      userRoutes        <- Resource.pure[F, UserRoutes[F]](new UserRoutes[F](userAlgebra))
      userLoginRoutes   <- Resource.pure[F, UserLoginRoutes[F]](new UserLoginRoutes[F](userAuthAlgebra))
      userAccountRoutes <- Resource.pure[F, UserAccountRoutes[F]](new UserAccountRoutes[F](userAccountOrganizer))
    } yield new UserAPI[F] {

      override def authedRoutes: AuthCtxRoutes[F] =
        NonEmptyList.of(userRoutes.authedRoutes, userAccountRoutes.authedRoutes).reduceK

      override def routes: HttpRoutes[F] = NonEmptyList.of(userAccountRoutes.routes, userLoginRoutes.routes).reduceK
    }

}
