package pms

import org.typelevel.log4cats

/** @author Lorand Szakacs, https://github.com/lorandszakacs
  * @since 25 Mar 2019
  */
package object logger {
  type Logger[F[_]] = log4cats.SelfAwareStructuredLogger[F]
  val Logger: log4cats.slf4j.Slf4jLogger.type = log4cats.slf4j.Slf4jLogger

}
