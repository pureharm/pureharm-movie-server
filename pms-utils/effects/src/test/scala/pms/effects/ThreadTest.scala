package pms.effects

import pms.effects.implicits._
import java.util.concurrent.TimeUnit

import org.specs2.mutable.Specification

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

/**
  *
  * @author Lorand Szakacs, https://github.com/lorandszakacs
  * @since 25 Mar 2019
  *
  */
final class ThreadTest extends Specification {

  implicit val ec1:           ExecutionContext = ExecutionContext.global
  implicit private val timer: Timer[IO]        = IO.timer(ExecutionContext.global)
  implicit private val cs:    ContextShift[IO] = IO.contextShift(ExecutionContext.global)
  private val blockingEC = ExecutionContext.fromExecutor(
    java.util.concurrent.Executors.newCachedThreadPool(),
  ): ExecutionContext

  private val interval      = 100 millis
  private val stuffDuration = 75 millis

  "stuff" >> {
    val ls = List.range(1, 200)

    val x = Future.sequence(
      ls.map(
        i =>
          Future.apply[Unit] {
            println(s"————— this is future: $i — tid=${Thread.currentThread().getId}")
            Future.apply {
              println(s"this is the other future tid=${Thread.currentThread().getId}")
              List.range(1, 900000).map(_.toString)
            }(blockingEC)
          },
      ),
    )

    Await.result(x, Duration.Inf)
    1 must_== (1)
  }

  "better stuff" >> {
    def firstIO(i: Int): IO[String] = {
      for {
        t1 <- getThreadName
        _  <- p(s"First IO i=$i — $t1")
        is <- cs.evalOn(blockingEC)(secondIO(i))
      } yield is
    }

    def secondIO(i: Int): IO[String] = {
      for {
        t2  <- getThreadName
        _   <- p(s"Nested IO i=$i — $t2")
        _   <- IO(report(i)(timer.sleep(2 seconds)))
        fib <- cs.evalOn(ec1)(report(i)(timer.sleep(1 second) *> IO.pure(i.toString)).start)
        is  <- fib.join
      } yield is
    }

    val result: IO[List[String]] = List.range(1, 200).parTraverse(firstIO)

    println {
      result.unsafeRunSync().mkString(",\n")
    }

    1 must_== 1
  }

  def report[T](i: Int)(fi: IO[T]): IO[T] = {
    getThreadName.flatMap { t3 =>
      p(s"going to sleep on i=$i — $t3") >> fi <* p(s"awakening on i=$i — $t3")
    }
  }

  def p(string: String): IO[Unit] = IO(println(string))
  def getThreadName: IO[String] = IO(Thread.currentThread().getName)
}
