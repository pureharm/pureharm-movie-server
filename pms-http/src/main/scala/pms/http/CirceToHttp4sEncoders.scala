package pms.http

import cats.data.EitherT
import org.http4s._
import org.http4s.circe._
import pms.effects._
import pms.json.{Decoder, Encoder}

/**
  *
  * @author Lorand Szakacs, https://github.com/lorandszakacs
  * @since 26 Jun 2018
  *
  */
trait CirceToHttp4sEncoders {

  implicit def asyncEntityJsonEncoder[F[_], T](
    implicit
    codec: Encoder[T],
    async: Sync[F]
  ): EntityEncoder[F, T] =
    CirceInstances
      .withPrinter(
        busymachines.json.PrettyJson.noSpacesNoNulls
      )
      .jsonEncoder[F]
      .contramap(codec.apply)

  implicit def asyncEntityJsonDecoder[F[_], T](
    implicit
    codec: Decoder[T],
    async: Async[F]
  ): EntityDecoder[F, T] =
    CirceInstances.defaultJsonDecoder.flatMapR { json =>
      EitherT.fromEither[F](codec.decodeJson(json).left.map(cd => org.http4s.InvalidMessageBodyFailure(cd.message)))
    }

}
