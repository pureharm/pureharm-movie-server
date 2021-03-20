package pms.json

import java.time.LocalDate
import pms._

/** @author Lorand Szakacs, https://github.com/lorandszakacs
  * @since 25 Jun 2018
  */
trait JavaTimeJson {

  implicit val localDateCirceCodec: Codec[LocalDate] = Codec.from(
    Decoder
      .apply[String]
      .map(s => LocalDate.parse(s, TimeFormatters.LocalDateFormatter)),
    Encoder
      .apply[String]
      .contramap(m => m.format(TimeFormatters.LocalDateFormatter)),
  )

}
