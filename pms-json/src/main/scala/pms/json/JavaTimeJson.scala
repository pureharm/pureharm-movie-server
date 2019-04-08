package pms.json

import java.time.LocalDate
import pms.core._

/**
  *
  * @author Lorand Szakacs, https://github.com/lorandszakacs
  * @since 25 Jun 2018
  *
  */
object JavaTimeJson extends JavaTimeJson

trait JavaTimeJson {

  implicit val localDateCirceCodec: Codec[LocalDate] = Codec.instance(
    Encoder.apply[String].contramap(m => m.format(TimeFormatters.LocalDateFormatter)),
    Decoder.apply[String].map(s => LocalDate.parse(s, TimeFormatters.LocalDateFormatter)),
  )

}
