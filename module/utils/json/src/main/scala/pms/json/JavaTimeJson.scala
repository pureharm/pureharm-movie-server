package pms.json

import java.time.LocalDate
import pms._

/** @author Lorand Szakacs, https://github.com/lorandszakacs
  * @since 25 Jun 2018
  */
trait JavaTimeJson {

  implicit val encodeLocalDate: io.circe.Encoder[LocalDate] =
    io.circe.Encoder[String].contramap(m => m.format(TimeFormatters.LocalDateFormatter))

  implicit val localDateCirceDecoder: io.circe.Decoder[LocalDate] =
    io.circe.Decoder[String].emapTry(s => Try(LocalDate.parse(s, TimeFormatters.LocalDateFormatter)))

}
