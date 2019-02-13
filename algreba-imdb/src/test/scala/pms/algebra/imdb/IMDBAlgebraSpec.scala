package pms.algebra.imdb

import cats.effect.IO
import java.time.Year

import net.ruippeixotog.scalascraper.model.Document
import pms.algebra.imdb.extra.RateLimiter
import scala.concurrent.duration._
import monix.execution.Scheduler.Implicits.global

class IMDBAlgebraSpec extends org.specs2.mutable.Specification {

//  private val rateLimiter = RateLimiter[IO, Document](1.seconds, 1)
//  private val imdbAlgebra = new impl.AsyncIMDBAlgebraImpl[IO](rateLimiter)
//
//  "Searching for" >> {
//    "Inception must return ('Începutul', 2010)" >> {
//      val inceptionMovie = Option(IMDBMovie(IMDBTitle("Începutul"), Option(ReleaseYear(Year.of(2010)))))
//      val result         = imdbAlgebra.scrapeMovieByTitle(TitleQuery("Inception"))
//      result.unsafeRunSync() mustEqual inceptionMovie
//    }
//
//    "Die Hard must return ('Greu de ucis', 1988)" >> {
//      val dieHardMovie = Option(IMDBMovie(IMDBTitle("Greu de ucis"), Option(ReleaseYear(Year.of(1988)))))
//      val result       = imdbAlgebra.scrapeMovieByTitle(TitleQuery("Die Hard"))
//      result.unsafeRunSync() mustEqual dieHardMovie
//    }
//
//    "Year Without God must return ('Year Without God', None)" >> {
//      val movieWithoutYear = Option(IMDBMovie(IMDBTitle("Year Without God"), None))
//      val result           = imdbAlgebra.scrapeMovieByTitle(TitleQuery("Year Without God"))
//      result.unsafeRunSync() mustEqual movieWithoutYear
//    }
//
//    "asdhashda must return None" >> {
//      val result = imdbAlgebra.scrapeMovieByTitle(TitleQuery("asdhashda"))
//      result.unsafeRunSync() mustEqual None
//    }
//  }
//
//  "Sending 5 `consecutive` requests should take more than 5000ms with current rate limiter configuration" >> {
//    val startTime = System.currentTimeMillis()
//    for (_ <- 0 to 4) {
//      imdbAlgebra.scrapeMovieByTitle(TitleQuery("Inception")).unsafeRunSync()
//    }
//    val elapsed = System.currentTimeMillis() - startTime
//    elapsed must be_>=(5000L)
//  }
}
