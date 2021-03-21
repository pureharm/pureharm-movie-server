package pms.json

import pms._
import pms.time._

/** @author Lorand Szakacs, https://github.com/lorandszakacs
  * @since 25 Jun 2018
  */
trait JavaTimeJson {

  implicit val encodeLocalDate:       io.circe.Encoder[LocalDate] =
    io.circe.Encoder[String].contramap((m: LocalDate) => m.show)

  implicit val localDateCirceDecoder: io.circe.Decoder[LocalDate] =
    io.circe.Decoder[String].emapTry(LocalDate.fromString[Try])

}
