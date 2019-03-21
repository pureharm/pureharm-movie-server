package pms.algebra.imdb.extra

import cats.effect.concurrent.Semaphore
import cats.implicits._

import scala.concurrent.duration._
import cats.effect.{Concurrent, Timer}

/**
  *
  * Used to control the rate of network requests by limiting the requests ratio per unit of time.
  * Implemented using a queue of outgoing requests which are scheduled to execute.
  *
  * @param interval  time unit between `size` consecutive requests
  * @param size      number of requests allowed to be sent in an `interval`
  * @param scheduler used to schedule the requests
  * @tparam F type used to initialize Rate Limiter.
  * @tparam T type of expected response
  */
final private[imdb] class RateLimiter[F[_]: Timer: Concurrent, T] private (
  val interval:  FiniteDuration,
  val semaphore: Semaphore[F]
) {

  def throttle(f: F[T]): F[T] =
    for {
      acquireTime <- acquireSemaphore
      res         <- f.onError { case _ => delayLogic(acquireTime) }
      _           <- delayLogic(acquireTime)
    } yield res

  private def acquireSemaphore: F[FiniteDuration] =
    for {
      _   <- semaphore.acquire
      now <- timeNow
    } yield now

  private def delayLogic(acquireTime: FiniteDuration): F[Unit] =
    for {
      now <- timeNow
      _ <- if (isWithinInterval(acquireTime, now))
            Timer[F].sleep(acquireTime.max(now) - acquireTime.min(now))
          else
            Concurrent[F].unit
      _ <- semaphore.release
    } yield ()

  private def isWithinInterval(acquireTime: FiniteDuration, now: FiniteDuration): Boolean =
    acquireTime - now < interval

  private def timeNow: F[FiniteDuration] =
    Timer[F].clock.monotonic(interval.unit).map(l => FiniteDuration(l, interval.unit))

}

object RateLimiter {

  def async[F[_]: Timer: Concurrent, T](
    interval: FiniteDuration,
    size:     Long
  ): F[RateLimiter[F, T]] = {
    for {
      sem <- Semaphore(size)
    } yield
      new RateLimiter[F, T](
        interval  = interval,
        semaphore = sem
      )
  }

}
