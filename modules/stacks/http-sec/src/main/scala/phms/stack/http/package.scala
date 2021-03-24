package phms.stack

import org.http4s.AuthedRoutes
import org.http4s.server.AuthMiddleware
import phms.algebra.user._
import phms.http._

/** @author Lorand Szakacs, https://github.com/lorandszakacs
  * @since 26 Jun 2018
  */
package object http extends Http4sCirceInstances with JavaTimeQueryParameterDecoders {
  type AuthCtxMiddleware[F[_]] = AuthMiddleware[F, AuthCtx]
  type AuthCtxRoutes[F[_]]     = AuthedRoutes[AuthCtx, F]
}
