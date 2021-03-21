package pms.time

import pms._
import java.{time => jt}

object LocalDate {
  def now[F[_]](implicit t: Time[F]): F[LocalDate] = t.LocalDate.now

  //TODO: adapt error
  def fromString[F[_]](s: String)(implicit F: ApplicativeThrow[F]): F[LocalDate] =
    F.catchNonFatal(jt.LocalDate.parse(s, TimeFormatters.LocalDateFormatter))
}
