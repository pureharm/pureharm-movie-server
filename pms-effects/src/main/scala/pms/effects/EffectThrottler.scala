package pms.effects

import cats.effect._
import cats.effect.concurrent.Semaphore
import cats.implicits._

import scala.concurrent.duration._

/**
  *
  * Used to control the rate at which Fs are computed by fixing the amount of Fs
  * to be executed in the given time interval.
  *
  * @param interval  time unit between `size` consecutive requests
  * @param semaphore bounded by the number of Fs allowed to be executed in the configured `interval`
  */
final class EffectThrottler[F[_]: Timer: Concurrent, T] private (
  private val interval:  FiniteDuration,
  private val semaphore: Semaphore[F]
) {

  private val F = Concurrent.apply[F]

  /**
    * Returns an F that will be "slowed" time to the configured rate
    * of execution.
    */
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
            F.unit
      _ <- semaphore.release
    } yield ()

  private def isWithinInterval(acquireTime: FiniteDuration, now: FiniteDuration): Boolean =
    acquireTime - now < interval

  private def timeNow: F[FiniteDuration] =
    Timer[F].clock.monotonic(interval.unit).map(l => FiniteDuration(l, interval.unit))

}

object EffectThrottler {

  def concurrent[F[_]: Timer: Concurrent, T](
    interval: FiniteDuration,
    amount:   Long
  ): F[EffectThrottler[F, T]] = {
    for {
      sem <- Semaphore(amount)
    } yield
      new EffectThrottler[F, T](
        interval  = interval,
        semaphore = sem
      )
  }

}
