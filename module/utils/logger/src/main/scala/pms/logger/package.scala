package pms

import io.chrisdavenport.log4cats

/**
  *
  * @author Lorand Szakacs, https://github.com/lorandszakacs
  * @since 25 Mar 2019
  *
  */
package object logger {
  type PMSLogger[F[_]] = log4cats.SelfAwareStructuredLogger[F]
  val PMSLogger: log4cats.slf4j.Slf4jLogger.type = log4cats.slf4j.Slf4jLogger

}
