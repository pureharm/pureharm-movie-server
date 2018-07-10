package pms.algebra.imdb.extra

import java.util.concurrent.ConcurrentLinkedQueue

import monix.execution.Scheduler
import pms.effects.{Async, Future}

import scala.concurrent.Promise
import scala.concurrent.duration._

/**
  *
  * Used to control the rate of network requests by limiting the requests ratio per unit of time.
  * Implemented using a queue of outgoing requests which are scheduled to execute.
  * @param interval time unit between `size` consecutive requests
  * @param size number of requests allowed to be sent in an `interval`
  * @param scheduler used to schedule the requests
  * @tparam F type used to initialize Rate Limiter.
  * @tparam T type of expected response
  */
final case class RateLimiter[F[_]: Async, T] private (interval: FiniteDuration, size: Int)(
  implicit val scheduler: Scheduler
) {

  private val requestQueue = new ConcurrentLinkedQueue[() => Promise[T]]()

  scheduler.scheduleAtFixedRate(0.millis, interval) {
    for (_ <- 0 until size if !requestQueue.isEmpty) {
      val request = requestQueue.poll()
      request.apply()
    }
  }

  def addToQueue(f: => T): Future[T] = {
    val promise = Promise[T]()
    requestQueue.add(() => { promise.completeWith(Future { f }) })
    promise.future
  }
}

object RateLimiter {

  def async[F[_]: Async, T](interval: FiniteDuration, size: Int)(implicit scheduler: Scheduler): RateLimiter[F, T] = {
    new RateLimiter[F, T](interval, size)
  }
}
