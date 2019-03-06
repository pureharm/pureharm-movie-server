package pms.algebra.imdb.extra

import java.util.concurrent.ConcurrentLinkedQueue

import cats.effect.concurrent.{Deferred, Ref, Semaphore}
import monix.execution.Scheduler
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
  //  // might be replaceable with cats.effect.Timer
  //  private val requestQueue = new ConcurrentLinkedQueue[() => Promise[T]]() ///care e faza cu parametrizarea?
  //
  //  /* scheduler.scheduleAtFixedRate(0.millis, interval) {
  //     for (_ <- 0 until size if !requestQueue.isEmpty) {
  //       val request = requestQueue.poll()
  //       request.apply()
  //     }
  //   }*/
  //
  //  Async[F].delay {
  //    Timer.apply.sleep(interval) {
  //      for (_ <- 0 until size if !requestQueue.isEmpty) {
  //        val request = requestQueue.poll()
  //        request.apply()
  //      }
  //    }
  //    //    def example(thunk: => T): F[T] = Async[F].delay(thunk)
  //    //
  //    //    example(throw new RuntimeException("3493-0259"))
  //
  //    def addToQueue(f: => T): Future[T] = {
  //      val promise = Promise[T]() ///declar a promise
  //      requestQueue.add(() => {
  //        promise.completeWith(Future {
  //          f
  //        })
  //      })
  //      //obtain the future that it complete
  //
  //      promise.future
  //    }
  //  }
}

object RateLimiter {

  def async[F[_]: Timer: Concurrent, T](
    interval: FiniteDuration,
    size:     Int
  ): F[RateLimiter[F, T]] = {
    for {
      sem <- Semaphore(size)
    } yield
      new RateLimiter[F, T](
        interval  = interval,
        semaphore = sem
      )
  }

  //  def async[F[_]: Async, T](interval: FiniteDuration, size: Int)(implicit scheduler: Scheduler): RateLimiter[F, T] = {
  //    new RateLimiter[F, T](interval, size)
  //  }
}
