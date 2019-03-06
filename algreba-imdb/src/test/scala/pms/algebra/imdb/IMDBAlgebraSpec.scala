package pms.algebra.imdb

import java.time.Year

import net.ruippeixotog.scalascraper.model.Document
import pms.algebra.imdb.extra.RateLimiter
import scala.concurrent.duration._
import monix.execution.Scheduler.Implicits.global

import cats.effect.{IO, Timer}

class IMDBAlgebraSpec extends org.specs2.mutable.Specification {

  implicit val timer: Timer[IO] = IO.timer(global)

  "IMDBAlgebra" should {
    val rateLimiter = RateLimiter.async[IO, Document](1.seconds, 1)

    val imdbAlgebra = new impl.AsyncIMDBAlgebraImpl[IO](rateLimiter.unsafeRunSync())

    """have a get movie title from imdb function""" in {

      val inceptionMovie = Option(IMDBMovie(IMDBTitle("Începutul"), Option(ReleaseYear(Year.of(2010)))))
      val result         = imdbAlgebra.scrapeMovieByTitle(TitleQuery("Inception"))

      result.unsafeRunSync() mustEqual inceptionMovie
    }

    "Die Hard must return ('Greu de ucis', 1988)" >> {
      val dieHardMovie = Option(IMDBMovie(IMDBTitle("Greu de ucis"), Option(ReleaseYear(Year.of(1988)))))
      val result       = imdbAlgebra.scrapeMovieByTitle(TitleQuery("Die Hard"))
      result.unsafeRunSync() mustEqual dieHardMovie
    }

    "Year Without God must return ('Year Without God', None)" >> {
      val movieWithoutYear = Option(IMDBMovie(IMDBTitle("Year Without God"), None))
      val result           = imdbAlgebra.scrapeMovieByTitle(TitleQuery("Year Without God"))
      result.unsafeRunSync() mustEqual movieWithoutYear
    }

    "asdhashda must return None" >> {
      val result = imdbAlgebra.scrapeMovieByTitle(TitleQuery("asdhashda"))
      result.unsafeRunSync() mustEqual None
    }
  }

  "Sending 5 `consecutive` requests should take more than 5000ms with current rate limiter configuration" >> {
    val rateLimiter = RateLimiter.async[IO, Document](1.seconds, 1)

    val imdbAlgebra = new impl.AsyncIMDBAlgebraImpl[IO](rateLimiter.unsafeRunSync())

    val startTime = System.currentTimeMillis()

    for (_ <- 0 to 4) {
      imdbAlgebra.scrapeMovieByTitle(TitleQuery("Inception")).unsafeRunSync()
    }
    val elapsed = System.currentTimeMillis() - startTime
    elapsed must be_>=(5000L)
  }
}
