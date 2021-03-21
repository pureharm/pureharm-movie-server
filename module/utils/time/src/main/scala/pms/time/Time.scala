package pms.time

import pms._
import java.{time => jt}

sealed trait Time[F[_]] {

  implicit protected def sync: Sync[F]

  def zoneOffset: ZoneOffset

  object LocalDate {
    def now: F[LocalDate] = sync.delay(jt.LocalDate.now())
    def fromString(s: String): F[LocalDate] = pms.time.LocalDate.fromString[F](s)
  }

  def now: F[OffsetDateTime] = sync.delay(jt.OffsetDateTime.now(zoneOffset))
}

object Time {

  def resource[F[_]](implicit F: Sync[F]): Resource[F, Time[F]] =
    new Time[F] {
      implicit override protected val sync: Sync[F] = F

      override val zoneOffset: ZoneOffset = jt.ZoneOffset.UTC
    }.pure[Resource[F, *]]
}
