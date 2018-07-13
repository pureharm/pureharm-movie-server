package pms.http

import org.http4s._
import org.http4s.circe.CirceInstances
import pms.effects._
import pms.json.{Decoder, Encoder}

/**
  *
  * @author Lorand Szakacs, https://github.com/lorandszakacs
  * @since 26 Jun 2018
  *
  */
trait CirceToHttp4sEncoders {
  private val circeInstances = CirceInstances
    .withPrinter(
      busymachines.json.PrettyJson.noSpacesNoNulls
    )

  implicit def syncEntityJsonEncoder[F[_], T](
    implicit
    codec: Encoder[T],
    sync:  Sync[F]
  ): EntityEncoder[F, T] = circeInstances.jsonEncoderOf[F, T]

  implicit def syncEntityJsonDecoder[F[_], T](
    implicit
    codec: Decoder[T],
    sync:  Sync[F]
  ): EntityDecoder[F, T] = circeInstances.jsonOf[F, T]

}
