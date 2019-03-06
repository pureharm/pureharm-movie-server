package pms.algebra.imdb

import cats.effect.{IO, Timer}
import pms.algebra.imdb.extra.RateLimiter
import monix.execution.Scheduler.Implicits.global
import scala.concurrent.duration._

import scala.concurrent.duration.{FiniteDuration, NANOSECONDS}

class RateLimiterSpec extends org.specs2.mutable.Specification {

  implicit val timer: Timer[IO] = IO.timer(global)

  "Verify RateLimiter independenly" >> {
    val rateLimiter: RateLimiter[IO, Unit] = RateLimiter.async[IO, Unit](1.seconds, 1).unsafeRunSync()

    def doSth(): IO[Unit] = Timer[IO].sleep(FiniteDuration(2000, NANOSECONDS))

    val startTime = System.currentTimeMillis()

    for (_ <- 0 to 4) {
      rateLimiter.throttle(doSth())
    }
    val elapsed = System.currentTimeMillis() - startTime
    elapsed must be_>=(5000L)

  }
}
