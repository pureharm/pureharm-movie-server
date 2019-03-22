package pms.algebra.imdb

import cats.effect.{ContextShift, Timer}
import pms.algebra.imdb.extra.RateLimiter
import monix.execution.Scheduler.Implicits.global

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._
import scala.concurrent.duration.{FiniteDuration, MILLISECONDS}
import pms.effects._

class RateLimiterSpec extends org.specs2.mutable.Specification {
  implicit val timer: Timer[IO]        = IO.timer(global)
  implicit val cs:    ContextShift[IO] = IO.contextShift(ExecutionContext.global)
  implicit def F:     Concurrent[IO]   = Concurrent.apply[IO]

  "Verify RateLimiter independenly" >> {
    val rateLimiter: RateLimiter[IO, Unit] = RateLimiter.async[IO, Unit](1.seconds, 1).unsafeRunSync()

    def doSth(): IO[Unit] = Timer[IO].sleep(FiniteDuration(800, MILLISECONDS))

    val startTime = System.currentTimeMillis()

    for (_ <- 0 to 4) {
      rateLimiter.throttle(doSth()).unsafeRunSync()
    }
    val elapsed = System.currentTimeMillis() - startTime
    elapsed must be_>=(5000L)

  }
}
