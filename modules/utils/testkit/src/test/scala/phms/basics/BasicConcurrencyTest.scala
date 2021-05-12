package phms.basics

import cats.effect.FiberIO
import phms._
import phms.test._

import scala.concurrent.duration._

final class BasicConcurrencyTest extends PHMSTest {

  private def getThreadName[F[_]](implicit F: Sync[F]): F[String] = F.delay(java.lang.Thread.currentThread().getName)

  private def printCPUs[F[_]](implicit F: Sync[F], console: Console[F]): F[Unit] = for {
    procs <- F.delay(java.lang.Runtime.getRuntime.availableProcessors())
    _     <- console.println(s"available cpus: $procs")
  } yield ()

  test("print CPU cores".ignore) {
    printCPUs[IO]
  }

  test("print thread name".ignore) {
    getThreadName[IO].flatMap(IO.println)
  }

  test("test out some fibers".ignore) {
    def printThreadName(msg: String, d: FiniteDuration): IO[Unit] = for {
      tn <- getThreadName[IO]
      _  <- IO.sleep(d).guaranteeCase {
        case Outcome.Succeeded(_) => IO.println(s"success => $msg: $tn")
        case Outcome.Canceled()   => IO.println(s"cancel => $msg: $tn")
        case Outcome.Errored(_)   => IO.println(s"error => $msg: $tn")
      }
    } yield ()

    val result =
      for {
        fiber1 <- printThreadName("fiber1", 10.millis).start
        fiber2 <- printThreadName("fiber2", 30.millis).start
        _      <- fiber1.join
        _      <- fiber2.join
        _      <- printThreadName("mainFiber", 0.millis)
      } yield ()

    result.timed
      .flatTap { case (dur, _) => IO.println(s"Finished after: ${dur.toMillis}") }

  }

  test("test out many fibers".ignore) {
    def printThreadName(msg: String, d: FiniteDuration): IO[Unit] = for {
      tn <- getThreadName[IO]
      _  <- IO.sleep(d).guaranteeCase {
        case Outcome.Succeeded(_) => IO.println(s"success => $msg: $tn")
        case Outcome.Canceled()   => IO.println(s"cancel => $msg: $tn")
        case Outcome.Errored(_)   => IO.println(s"error => $msg: $tn")
      }
    } yield ()

    val result =
      List.range(0, 10000).parTraverse { idx =>
        for {
          fiber1 <- printThreadName(s"fiber1-$idx", 1000.millis).start
          fiber2 <- printThreadName(s"fiber2-$idx", 3000.millis).start
          _      <- fiber1.join
          _      <- fiber2.join
          _      <- printThreadName("mainFiber", 0.millis)
        } yield ()
      }

    result.timed
      .flatTap { case (dur, _) => IO.println(s"Finished after: ${dur.toMillis}") }

  }

  test("test out many fibers but w/ blocking computation on compute pool".ignore) {
    def printThreadName(msg: String, d: FiniteDuration): IO[Unit] = for {
      tn <- getThreadName[IO]
      _  <- IO {
        val tn = java.lang.Thread.currentThread().getName
        scala.Console.println(s"$msg sleeps for $d on thread: $tn")
        java.lang.Thread.sleep(d.toMillis)
      }
    } yield ()

    val result =
      List.range(0, 1000).parTraverse { idx =>
        for {
          tn     <- getThreadName[IO]
          _      <- IO.println(s"On compute pool for fiber $idx -- $tn")
          fiber1 <- printThreadName(s"fiber1-$idx", 2000.millis).start
          fiber2 <- printThreadName(s"fiber2-$idx", 3000.millis).start
          _      <- List.range(0, 1000).parTraverse(idx2 => idx2.pure[IO])
          _      <- IO.println(s"$idx Finished compute stuff in parallel! for")
          _      <- fiber1.join
          _      <- fiber2.join
          _      <- printThreadName("mainFiber", 0.millis)
        } yield ()
      }

    result.timed
      .flatTap { case (dur, _) => IO.println(s"Finished after: ${dur.toMillis}") }

  }

  test("test out many fibers but w/ blocking computation on blocking pool".ignore) {
    def printThreadName(msg: String, d: FiniteDuration): IO[Unit] = for {
      tn <- getThreadName[IO]
      _  <- IO.blocking {
        val tn = java.lang.Thread.currentThread().getName
        scala.Console.println(s"$msg sleeps for $d on thread: $tn")
        java.lang.Thread.sleep(d.toMillis)
      }
    } yield ()

    val result =
      List.range(0, 1000).parTraverse { idx =>
        for {
          tn     <- getThreadName[IO]
          _      <- IO.println(s"On compute pool for fiber $idx -- $tn")
          fiber1 <- printThreadName(s"fiber1-$idx", 2000.millis).start
          fiber2 <- printThreadName(s"fiber2-$idx", 3000.millis).start
          _      <- List.range(0, 1000).parTraverse(idx2 => idx2.pure[IO])
          _      <- IO.println(s"$idx Finished compute stuff in parallel! for")
          _      <- fiber1.join
          _      <- fiber2.join
          _      <- printThreadName("mainFiber", 0.millis)
        } yield ()
      }

    result.timed
      .flatTap { case (dur, _) => IO.println(s"Finished after: ${dur.toMillis}") }

  }
}
