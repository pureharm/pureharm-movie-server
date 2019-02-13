package pms.algebra.imdb.extra

import java.util.concurrent.ConcurrentLinkedQueue

import cats.effect.concurrent.{Deferred, Ref}
import monix.execution.Scheduler
import cats.implicits._

import scala.concurrent.duration._
import cats.effect.{Async, Timer}

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
final private[imdb] class RateLimiter[F[_]: Timer: Async, T] private (
  val interval:            FiniteDuration,
  val size:                Int,
  private val initialTime: Ref[F, Long]
) {

  def throttle(f: F[T]): F[T] = ???

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

  def async[F[_]: Timer: Async, T](
    interval: FiniteDuration,
    size:     Int
  ): F[RateLimiter[F, T]] = {
    for {
      initTime <- Timer[F].clock.monotonic(interval.unit)
      ref      <- Ref.of[F, Long](initTime)
    } yield
      new RateLimiter[F, T](
        interval    = interval,
        size        = size,
        initialTime = ref
      )
  }

//  def async[F[_]: Async, T](interval: FiniteDuration, size: Int)(implicit scheduler: Scheduler): RateLimiter[F, T] = {
//    new RateLimiter[F, T](interval, size)
//  }
}
