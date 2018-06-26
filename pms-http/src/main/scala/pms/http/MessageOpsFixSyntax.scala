package pms.http

import cats.FlatMap
import org.http4s._

/**
  *
  * This is a workaround to the syntax of ``org.http4s.MessageOps#as``
  * conflicting with that of ``org.http4s.dsl.impl.Auth.as`` when
  * using [[AuthedService]] (which is 99% of the time)
  *
  * @author Lorand Szakacs, https://github.com/lorandszakacs
  * @since 26 Jun 2018
  *
  */
trait MessageOpsFixSyntax {

  implicit def messageOpsFixRequestFix[F[_]](r: Request[F]): MessageOpsFixRequestFix[F] =
    new MessageOpsFixRequestFix(r)

  implicit def messageOpsFixRequestFix[F[_], A](r: AuthedRequest[F, A]): MessageOpsFixAuthedReqFix[F, A] =
    new MessageOpsFixAuthedReqFix(r)
}

final class MessageOpsFixRequestFix[F[_]](r: Request[F]) {

  def bodyAs[T](implicit F: FlatMap[F], decoder: EntityDecoder[F, T]): F[T] = r.as[T]
}

final class MessageOpsFixAuthedReqFix[F[_], A](r: AuthedRequest[F, A]) {

  def bodyAs[T](implicit F: FlatMap[F], decoder: EntityDecoder[F, T]): F[T] = r.req.as[T]
}
