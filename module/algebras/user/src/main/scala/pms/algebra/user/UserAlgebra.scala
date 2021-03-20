package pms.algebra.user

import pms.algebra.user.impl.UserAlgebraImpl
import pms._
import pms.db._

/** @author Lorand Szakacs, https://github.com/lorandszakacs
  * @since 20 Jun 2018
  */
trait UserAlgebra[F[_]] {

  def findUser(id: UserID)(implicit auth: AuthCtx): F[Option[User]]

}

object UserAlgebra {

  def resource[F[_]](implicit transactor: Transactor[F], F: Async[F]): Resource[F, UserAlgebra[F]] =
    Resource.pure(new UserAlgebraImpl())
}
