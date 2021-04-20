package phms

import munit._

import scala.concurrent.duration._

class TemporalOpsTest extends CatsEffectSuite {
  test("fixed time in happy case") {
    val x: IO[(FiniteDuration, Unit)] = IO.unit.minTime(50.millis).timed
    x.map { case (dur, _) =>
      assert(dur >= 50.millis)
    }
  }

  test("fixed time in error case") {
    val x: IO[(FiniteDuration, Attempt[Unit])] =
      IO.raiseError[Unit](InvalidInputAnomaly("I failed")).attempt.minTime(50.millis).timed
    x.map { case (dur, _) =>
      assert(dur >= 50.millis)
    }
  }
}
