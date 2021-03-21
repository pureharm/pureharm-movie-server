package pms.http

import org.http4s.{ParseFailure, QueryParamDecoder}
import pms._
import pms.time._

/** @author Lorand Szakacs, https://github.com/lorandszakacs
  * @since 28 Jun 2018
  */
trait JavaTimeQueryParameterDecoders {

  implicit val localDateQueryParamDecoder: QueryParamDecoder[LocalDate] =
    QueryParamDecoder.stringQueryParamDecoder.emap(s =>
      LocalDate
        .fromString[Attempt](s)
        .leftMap(t =>
          ParseFailure(
            sanitized = "Failed to parse query param for LocalDate",
            details   = t.getMessage,
          )
        )
    )
}
