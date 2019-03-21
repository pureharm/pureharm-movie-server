package pms.algebra

import org.http4s.AuthedService
import org.http4s.server.AuthMiddleware
import pms.algebra.user.AuthCtx
import pms.http.{Http4sCirceInstances, JavaTimeQueryParameterDecoders}

/**
  *
  * @author Lorand Szakacs, https://github.com/lorandszakacs
  * @since 26 Jun 2018
  *
  */
package object http extends Http4sCirceInstances with JavaTimeQueryParameterDecoders {
  type AuthCtxMiddleware[F[_]] = AuthMiddleware[F,      AuthCtx]
  type AuthCtxRoutes[F[_]]     = AuthedService[AuthCtx, F]
}
