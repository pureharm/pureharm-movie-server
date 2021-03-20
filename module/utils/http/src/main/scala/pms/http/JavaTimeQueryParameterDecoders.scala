package pms.http

import java.time.LocalDate

import org.http4s.QueryParamDecoder

import pms.TimeFormatters

/**
  *
  * @author Lorand Szakacs, https://github.com/lorandszakacs
  * @since 28 Jun 2018
  *
  */
trait JavaTimeQueryParameterDecoders {

  implicit val localDateQueryParamDecoder: QueryParamDecoder[LocalDate] =
    QueryParamDecoder.stringQueryParamDecoder.map(s => LocalDate.parse(s, TimeFormatters.LocalDateFormatter))
}
