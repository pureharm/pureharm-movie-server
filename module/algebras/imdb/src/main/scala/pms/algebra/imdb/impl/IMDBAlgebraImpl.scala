package pms.algebra.imdb.impl

import java.time.Year
import net.ruippeixotog.scalascraper.browser.JsoupBrowser
import net.ruippeixotog.scalascraper.dsl.DSL.Extract._
import net.ruippeixotog.scalascraper.dsl.DSL._
import net.ruippeixotog.scalascraper.model.Document
import pms.algebra.imdb._
import pms._

/** @author Lorand Szakacs, https://github.com/lorandszakacs
  * @since 25 Jun 2018
  */
final private[imdb] class IMDBAlgebraImpl[F[_]](
  val throttler: EffectThrottler[F],
  val browser:   JsoupBrowser,
)(implicit
  val F:         Async[F]
) extends IMDBAlgebra[F] {

  override def scrapeMovieByTitle(title: TitleQuery): F[Option[IMDBMovie]] =
    for {
      doc       <- throttler.throttle[Document] {
        F.delay[Document](browser.get(s"https://imdb.com/find?q=$title&s=tt"))
      }
      imdbMovie <- doc match {
        case Left(_)      => F.pure(None)
        case Right(value) => F.delay(parseIMDBDocument(value))
      }
    } yield imdbMovie

  private def parseIMDBDocument(imdbDocument: Document): Option[IMDBMovie] =
    for {
      findList     <- imdbDocument.tryExtract(elementList(".findList tr"))
      firstElement <- findList.headOption
      resultText   <- firstElement.tryExtract(element(".result_text"))
      titleElement <- resultText.tryExtract(element("a"))
      title         = IMDBTitle(titleElement.text)
      resultTextStr = resultText.text
      year          = parseYear(resultTextStr)
    } yield IMDBMovie(title, year)

  private def parseYear(resultTextStr: String): Option[ReleaseYear] = {
    val yearStartPos = resultTextStr.indexOf("(")
    if (yearStartPos > 0)
      Attempt
        .catchNonFatal(ReleaseYear(Year.parse(resultTextStr.substring(yearStartPos + 1, yearStartPos + 5))))
        .toOption
    else None
  }
}
