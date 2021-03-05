package pms.core

import scala.concurrent.duration._

/**
  *
  * Used to control the rate at which Fs are computed by fixing the amount of Fs
  * to be executed in the given time interval.
  *
  * @param interval  time unit between `size` consecutive requests
  * @param semaphore bounded by the number of Fs allowed to be executed in the configured `interval`
  */
final class EffectThrottler[F[_]: Timer: Concurrent](
  private val interval: FiniteDuration,
  val semaphore:        Semaphore[F],
) {

  private val F = Concurrent.apply[F]

  /**
    * Returns an F that will be "slowed" time to the configured rate
    * of execution.
    */
  def throttle[T](f: F[T]): F[Attempt[T]] =
    for {
      _                   <- semaphore.acquire
      (duration, attempt) <- f.timedAttempt()
      _                   <- if (isWithinInterval(duration)) Timer[F].sleep(interval - duration) else F.unit
      _                   <- semaphore.release
    } yield attempt

  private def isWithinInterval(duration: FiniteDuration): Boolean =
    duration < interval

}

object EffectThrottler {

  def resource[F[_]: Timer: Concurrent](
    interval: FiniteDuration,
    amount:   Long,
  ): Resource[F, EffectThrottler[F]] =
    Resource.liftF {
      for {
        sem <- Semaphore(amount)
      } yield new EffectThrottler[F](
        interval  = interval,
        semaphore = sem,
      )
    }

}
