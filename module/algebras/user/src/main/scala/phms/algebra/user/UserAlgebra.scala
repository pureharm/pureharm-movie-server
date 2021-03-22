package phms.algebra.user

import phms.algebra.user.impl.UserAlgebraImpl
import phms._
import phms.db._

/** @author Lorand Szakacs, https://github.com/lorandszakacs
  * @since 20 Jun 2018
  */
trait UserAlgebra[F[_]] {

  def findUser(id: UserID)(implicit auth: AuthCtx): F[Option[User]]

}

object UserAlgebra {

  def resource[F[_]](implicit dbPool: DDPool[F], F: Async[F], sr: SecureRandom[F]): Resource[F, UserAlgebra[F]] =
    Resource.pure(new UserAlgebraImpl())
}
