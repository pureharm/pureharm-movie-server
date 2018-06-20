package pms.algebra.user

/**
  *
  * @author Lorand Szakacs, https://github.com/lorandszakacs
  * @since 20 Jun 2018
  *
  */
trait UserAlgebra[F[_]] {

  def findUser(id: UserID)(implicit auth: AuthCtx): F[Option[User]]

}

object UserAlgebra {
  import pms.effects._

  def async[F[_]: Async]: UserAlgebra[F] = new impl.AsyncAlgebraImpl[F]()
}
