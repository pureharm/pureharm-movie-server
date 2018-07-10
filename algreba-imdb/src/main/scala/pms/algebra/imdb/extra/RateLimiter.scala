package pms.algebra.imdb.extra

import java.util.concurrent.ConcurrentLinkedQueue

import monix.execution.Scheduler
import pms.effects.Future

import scala.concurrent.Promise
import scala.concurrent.duration._

case class RateLimiter[T](interval: FiniteDuration, size: Int)(
  implicit val scheduler:           Scheduler
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
