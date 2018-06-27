package pms.algebra

import org.http4s.AuthedService
import org.http4s.server.AuthMiddleware
import pms.algebra.user.AuthCtx
import pms.http.CirceToHttp4sEncoders

/**
  *
  * @author Lorand Szakacs, https://github.com/lorandszakacs
  * @since 26 Jun 2018
  *
  */
package object http extends CirceToHttp4sEncoders {
  type AuthCtxService[F[_]]    = AuthedService[AuthCtx, F]
  type AuthCtxMiddleware[F[_]] = AuthMiddleware[F,      AuthCtx]
}
