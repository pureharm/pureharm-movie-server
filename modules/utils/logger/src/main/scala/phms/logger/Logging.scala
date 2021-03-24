package phms.logger

import phms._

trait Logging[F[_]] {
  def of(a:    AnyRef): Logger[F]
  def named(s: String): Logger[F]
}

object Logging {

  def resource[F[_]: Sync]: Resource[F, Logging[F]] =
    new Logging[F] {
      override def of(a:    AnyRef): Logger[F] = Logger.getLoggerFromName(a.getClass.getCanonicalName.stripSuffix("$"))
      override def named(s: String): Logger[F] = Logger.getLoggerFromName(s)
    }.pure[Resource[F, *]]
}
