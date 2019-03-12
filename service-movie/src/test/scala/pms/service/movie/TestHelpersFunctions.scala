package pms.service.movie

import pms.effects._
import org.http4s._
import org.http4s.dsl.io._
import org.http4s.implicits._

object TestHelpersFunctions {

  // Return true if match succeeds; otherwise false
  def check[A](actual: IO[Response[IO]], expectedStatus: Status, expectedBody: Option[A])(
    implicit ev:       EntityDecoder[IO, A]
  ): Boolean = {
    val actualResp  = actual.unsafeRunSync
    val statusCheck = actualResp.status == expectedStatus
    val bodyCheck =
      expectedBody.fold[Boolean](actualResp.body.compile.toVector.unsafeRunSync.isEmpty)( // Verify Response's body is empty.
        expected => actualResp.as[A].unsafeRunSync == expected
      )
    statusCheck && bodyCheck
  }


//  def service[F[_]](repo: UserRepo[F])(
//    implicit F: Effect[F]
//  ): HttpRoutes[F] = HttpRoutes.of[F] {
//    case GET -> Root / "user" / id =>
//      repo.find(id).map {
//        case Some(user) => Response(status = Status.Ok).withEntity(user.asJson)
//        case None       => Response(status = Status.NotFound)
//      }
//  }
}
