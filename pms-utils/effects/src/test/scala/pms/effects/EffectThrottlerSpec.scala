package pms.effects

import pms.effects.implicits._
import java.util.concurrent.TimeUnit
import org.specs2.mutable.Specification
import scala.concurrent.duration._

/**
  *
  * @author Lorand Szakacs, https://github.com/lorandszakacs
  * @since 25 Mar 2019
  *
  */
final class EffectThrottlerSpec extends Specification {
  implicit private val timer: Timer[IO]        = IO.timer(ExecutionContext.global)
  implicit private val cs:    ContextShift[IO] = IO.contextShift(ExecutionContext.global)

  private val runs = List(0, 1, 2, 3, 4, 5, 6)

  private val interval      = 100 millis
  private val stuffDuration = 75 millis

  "throttle effects at 1F/100ms" >> {
    val throttler: EffectThrottler[IO] = EffectThrottler.concurrent[IO](interval, 1).unsafeRunSync()
    val minWaitTime = computeMinWaitTime(runs.length, interval, 1)

    val eff: IO[Unit] = runs.parTraverse(i => throttler.throttle(doStuff(i))).void
    val elapsed = unsafeMeasureTime(eff.unsafeRunSync())
    elapsed must be_>=(minWaitTime)
  }

  "throttle effects at 2F/100ms" >> {
    val throttler: EffectThrottler[IO] = EffectThrottler.concurrent[IO](interval, 2).unsafeRunSync()
    val minWaitTime = computeMinWaitTime(runs.length, interval, 2)

    val eff: IO[Unit] = runs.parTraverse(i => throttler.throttle(doStuff(i))).void
    val elapsed = unsafeMeasureTime(eff.unsafeRunSync())
    elapsed must be_>=(minWaitTime)
  }

  private def doStuff(it: Int): IO[Unit] =
    IO.delay(println(s"starting $it")) >>
      Timer[IO].sleep(stuffDuration) >>
      IO.delay(println(s"ending $it"))

  private def unsafeMeasureTime(thunk: => Unit): FiniteDuration = {
    val startTime = System.nanoTime()
    thunk
    val elapsed = System.nanoTime() - startTime
    FiniteDuration.apply(elapsed, TimeUnit.NANOSECONDS)
  }

  private def computeMinWaitTime(runs: Int, interval: FiniteDuration, size: Int): FiniteDuration =
    interval.*((runs / size).toLong)
}
