package pms.algebra.http

import cats.Applicative

import pms.algebra.user.AuthCtx

import org.http4s.{AuthedRequest, AuthedService, Response}

/**
  *
  * @author Lorand Szakacs, https://github.com/lorandszakacs
  * @since 26 Jun 2018
  *
  */
object AuthCtxRoutes {

  def apply[F[_]](
    pf:         PartialFunction[AuthedRequest[F, AuthCtx], F[Response[F]]]
  )(implicit F: Applicative[F]): AuthedService[AuthCtx, F] =
    AuthedService.apply(pf)
}
