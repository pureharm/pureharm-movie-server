package pms

package object time {
  type LocalDate      = java.time.LocalDate
  /** We always, always, by convention use this w/ UTC
    * in our application.
    */
  type OffsetDateTime = java.time.OffsetDateTime

  type ZoneOffset = java.time.ZoneOffset

  implicit val showLocalDate: Show[LocalDate] = Show.show(ld => ld.format(TimeFormatters.LocalDateFormatter))
}
