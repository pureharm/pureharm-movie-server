package pms.algebra

import org.http4s.AuthedService
import pms.algebra.user.AuthCtx
import pms.http.{CirceToHttp4sEncoders, MessageOpsFixSyntax}

/**
  *
  * @author Lorand Szakacs, https://github.com/lorandszakacs
  * @since 26 Jun 2018
  *
  */
package object http extends CirceToHttp4sEncoders with MessageOpsFixSyntax {
  type AuthCtxService[F[_]] = AuthedService[AuthCtx, F]
}
