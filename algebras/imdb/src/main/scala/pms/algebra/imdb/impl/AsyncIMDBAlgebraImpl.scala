package pms.algebra.imdb.impl

import net.ruippeixotog.scalascraper.dsl.DSL._
import net.ruippeixotog.scalascraper.dsl.DSL.Extract._
import net.ruippeixotog.scalascraper.browser.JsoupBrowser
import pms.effects._
import pms.algebra.imdb._
import cats.implicits._
import java.time.Year

import net.ruippeixotog.scalascraper.model.Document

/**
  *
  * @author Lorand Szakacs, https://github.com/lorandszakacs
  * @since 25 Jun 2018
  *
  */
final private[imdb] class AsyncIMDBAlgebraImpl[F[_]](
  val throttler: EffectThrottler[F],
)(
  implicit val F: Async[F],
) extends IMDBAlgebra[F] {

  override def scrapeMovieByTitle(title: TitleQuery): F[Option[IMDBMovie]] = {
    val browser = JsoupBrowser()
    for {
      doc <- throttler.throttle[Document] {
        F.delay[Document](browser.get(s"https://imdb.com/find?q=$title&s=tt"))
      }
      imdbMovie <- F.delay(parseIMDBDocument(doc))
    } yield imdbMovie
  }

  private def parseIMDBDocument(imdbDocument: Document): Option[IMDBMovie] = {
    for {
      findList     <- imdbDocument tryExtract elementList(".findList tr")
      firstElement <- findList.headOption
      resultText   <- firstElement tryExtract element(".result_text")
      titleElement <- resultText tryExtract element("a")
      title         = IMDBTitle(titleElement.text)
      resultTextStr = resultText.text
      year          = parseYear(resultTextStr)
    } yield IMDBMovie(title, year)
  }

  private def parseYear(resultTextStr: String): Option[ReleaseYear] = {
    val yearStartPos = resultTextStr.indexOf("(")
    if (yearStartPos > 0)
      Result[ReleaseYear](ReleaseYear(Year.parse(resultTextStr.substring(yearStartPos + 1, yearStartPos + 5)))).toOption
    else None
  }
}
