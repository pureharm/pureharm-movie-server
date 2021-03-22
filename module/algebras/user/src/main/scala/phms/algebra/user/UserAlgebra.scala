package phms.algebra.user

import phms.algebra.user.impl.UserAlgebraImpl
import phms._
import phms.time._
import phms.db._

/** @author Lorand Szakacs, https://github.com/lorandszakacs
  * @since 20 Jun 2018
  */
trait UserAlgebra[F[_]] {

  def findUser(id: UserID)(implicit auth: AuthCtx): F[Option[User]]

}

object UserAlgebra {

  def resource[F[_]](implicit
    dbPool: DBPool[F],
    F:      MonadCancelThrow[F],
    time:   Time[F],
    r:      Random[F],
    sr:     SecureRandom[F],
  ): Resource[F, UserAlgebra[F]] =
    Resource.pure[F, UserAlgebra[F]](new UserAlgebraImpl())
}
