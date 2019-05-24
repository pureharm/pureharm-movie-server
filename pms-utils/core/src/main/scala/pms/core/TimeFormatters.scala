package pms.core

import java.time.format.DateTimeFormatter

/**
  *
  * This object contains a format for all default representations
  * of anything [[java.time]]. This helps us keep everything
  * consistent across the application.
  *
  * @author Lorand Szakacs, https://github.com/lorandszakacs
  * @since 26 Jun 2018
  *
  */
object TimeFormatters {
  val LocalDateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
}
