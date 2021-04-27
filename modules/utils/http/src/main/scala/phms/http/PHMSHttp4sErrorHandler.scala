package phms.http

import busymachines.pureharm.anomaly.AnomalyLike
import org.http4s.*
import phms.*
import phms.logger.*

final class PHMSHttp4sErrorHandler[F[_]](using MonadThrow[F], Logging[F])
  extends PartialFunction[Throwable, F[Response[F]]] with Http4sCirceInstances {

  private val logger: Logger[F] = Logging[F].named("http4s.handler")

  private val function: PartialFunction[Throwable, F[Response[F]]] = {
    case c: Catastrophe =>
      for {
        _ <- logger.warn(c)(s"Catastrophe.")
        resp = Response[F](status = Status.InternalServerError).withEntity(fromAnomaly(c))
      } yield resp

    case an: AnomalyLike =>
      for {
        _ <- logger.debug(s"Error response.")
        resp = Response[F](status = Status.BadRequest).withEntity(fromAnomaly(an))
      } yield resp

    case t: java.util.concurrent.TimeoutException =>
      for {
        _ <- logger.trace(s"Ember triggered request timed out: ${t.getMessage}")
        resp = Response[F](status = Status.RequestTimeout)
          .withEntity(fromAnomaly(UnhandledCatastrophe(t)))
      } yield resp

    case t: org.http4s.InvalidMessageBodyFailure =>
      val anomaly = t.cause match {
        case Some(t: AnomalyLike) => t
        case Some(circe: io.circe.DecodingFailure) =>
          InvalidInputAnomaly(
            circe.message,
            parameters = Anomaly.Parameters("circeOps" -> circe.history.map(_.toString)),
          )
        case Some(t) => InvalidInputAnomaly(t.toString)
        case None    => InvalidInputAnomaly(t.toString)
      }
      for {
        _ <- logger.debug(t)(s"Malformed request body: \n$t")
        resp = Response[F](status = Status.RequestTimeout)
          .withEntity(fromAnomaly(anomaly))
      } yield resp

    case t: Throwable =>
      for {
        _ <- logger.warn(t)(s"Unhandled throwable: \n$t")
        resp = Response[F](status = Status.RequestTimeout)
          .withEntity(fromAnomaly(UnhandledCatastrophe(t)))
      } yield resp
  }

  private def fromAnomaly(an: AnomalyLike): PHMSHttp4sErrorHandler.ServerFailure = PHMSHttp4sErrorHandler.ServerFailure(
    id      = an.id.name,
    message = an.message,
    params  = an.parameters.view.mapValues(p => p.toString).toMap,
    cause   = an.getClass.getCanonicalName.some,
  )

  override def isDefinedAt(x: Throwable): Boolean        = function.isDefinedAt(x)
  override def apply(v1:      Throwable): F[Response[F]] = function(v1)
}

object PHMSHttp4sErrorHandler {

  def resource[F[_]](using MonadThrow[F], Logging[F]): Resource[F, PHMSHttp4sErrorHandler[F]] =
    new PHMSHttp4sErrorHandler[F]().pure[Resource[F, *]]

  /** TODO: replace w/ proper anomaly serialization
    */
  private case class ServerFailure(
    id:      String,
    message: String,
    params:  Map[String, String],
    cause:   Option[String],
  )

  private object ServerFailure {
    import phms.json.{*, given}
    given Codec[ServerFailure] = derive.codec[ServerFailure]
  }
}
