package pms.algebra.imdb.impl

import net.ruippeixotog.scalascraper.dsl.DSL._
import net.ruippeixotog.scalascraper.dsl.DSL.Extract._
import net.ruippeixotog.scalascraper.browser.JsoupBrowser
import pms.effects._
import pms.algebra.imdb._
import java.time.Year

import net.ruippeixotog.scalascraper.model.Document
import pms.algebra.imdb.extra.RateLimiter

/**
  *
  * @author Lorand Szakacs, https://github.com/lorandszakacs
  * @since 25 Jun 2018
  *
  */
final private[imdb] class AsyncIMDBAlgebraImpl[F[_]](val rateLimiter: RateLimiter[F, Document])(
  implicit val F:                                                     Async[F],
  implicit val scheduler:                                             Scheduler
) extends IMDBAlgebra[F] {

  override def scrapeMovieByTitle(title: TitleQuery): F[Option[IMDBMovie]] = {
    val browser = JsoupBrowser()
    val movie = IO.suspendFuture {
      for {
        doc <- rateLimiter.addToQueue {
                browser.get(s"https://imdb.com/find?q=$title&s=tt")
              }
        imdbMovie <- Future {
                      parseIMDBDocument(doc)
                    }
      } yield imdbMovie
    }
    F.liftIO(movie)
  }

  private def parseIMDBDocument(imdbDocument: Document): Option[IMDBMovie] = {
    for {
      findList     <- imdbDocument tryExtract elementList(".findList tr")
      firstElement <- findList.headOption
      resultText   <- firstElement tryExtract element(".result_text")
      titleElement <- resultText tryExtract element("a")
      title         = IMDBTitle(titleElement.text)
      resultTextStr = resultText.text
      yearStartPos  = resultTextStr.indexOf("(")
      year = if (yearStartPos > 0)
        Option(ReleaseYear(Year.parse(resultTextStr.substring(yearStartPos + 1, yearStartPos + 5))))
      else None
    } yield IMDBMovie(title, year)
  }
}
