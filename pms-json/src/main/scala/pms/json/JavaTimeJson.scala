package pms.json

import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
  *
  * @author Lorand Szakacs, https://github.com/lorandszakacs
  * @since 25 Jun 2018
  *
  */
object JavaTimeJson extends JavaTimeJson

trait JavaTimeJson {
  private val localDateFormatter = DateTimeFormatter.ofPattern("YYYY-MM-dd")

  implicit val localDateCirceCodec: Codec[LocalDate] = Codec.instance(
    Encoder.apply[String].contramap(m => m.format(localDateFormatter)),
    Decoder.apply[String].map(s => LocalDate.parse(s, localDateFormatter))
  )

}
