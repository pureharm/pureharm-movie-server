package pms.rest.user

import org.http4s.dsl._
import pms.algebra.http._
import pms.algebra.user._
import pms.core._


/**
  *
  * @author Lorand Szakacs, https://github.com/lorandszakacs
  * @since 26 Jun 2018
  *
  */
final class UserRoutes[F[_]](
  private val userAlgebra: UserAlgebra[F]
)(implicit
  val F:                   Async[F]
) extends Http4sDsl[F] with UserRoutesJSON {

  private val userRestRoutes: AuthCtxRoutes[F] = AuthCtxRoutes[F] {
    case GET -> Root / "user" / LongVar(userID) as user =>
      for {
        resp <- Ok(userAlgebra.findUser(UserID(userID))(user))
      } yield resp
  }

  val authedRoutes: AuthCtxRoutes[F] = userRestRoutes

}
