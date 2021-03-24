package phms.stack.http

import phms._
import phms.algebra.user.AuthCtx
import org.http4s.{AuthedRequest, AuthedRoutes, Response}

/** @author Lorand Szakacs, https://github.com/lorandszakacs
  * @since 26 Jun 2018
  */
object AuthCtxRoutes {

  def apply[F[_]](pf: PartialFunction[AuthedRequest[F, AuthCtx], F[Response[F]]])(implicit
    F:                Defer[F],
    FA:               Applicative[F],
  ): AuthedRoutes[AuthCtx, F] = AuthedRoutes.of(pf)
}
