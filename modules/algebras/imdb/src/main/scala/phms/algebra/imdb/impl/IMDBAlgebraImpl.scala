/*
 * Copyright 2021 BusyMachines
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package phms.algebra.imdb.impl

import java.time.Year

import phms.algebra.imdb.*
import phms.*

/** @author Lorand Szakacs, https://github.com/lorandszakacs
  * @since 25 Jun 2018
  */
final private[imdb] class IMDBAlgebraImpl[F[_]](
  val throttler: EffectThrottler[F],
  val browser:   JsoupWrapper[F],
)(using
  F:             Sync[F]
) extends IMDBAlgebra[F] {
  import net.ruippeixotog.scalascraper.model.Document

  override def scrapeMovieByTitle(title: TitleQuery): F[Option[IMDBMovie]] = for {
    docAttempt <- throttler
      .throttle[Document](browser.getDocument(s"https://imdb.com/find?q=$title&s=tt"))
      .attempt
    imdbMovie  <- docAttempt match {
      case Left(_)      => Option.empty.pure[F]
      case Right(value) => F.blocking(parseIMDBDocument(value))
    }
  } yield imdbMovie

  import net.ruippeixotog.scalascraper.model.Document
  // this import causes Scala 3 compiler crash because of dotty interop,
  // and we can't implement this method using its syntax. Simply drop the library
  // and use jsoup directly :shrug:
  // import net.ruippeixotog.scalascraper.dsl.DSL._

  private def parseIMDBDocument(imdbDocument: Document): Option[IMDBMovie] = ???
  // for {
  //   findList     <- imdbDocument.tryExtract(elementList(".findList tr"))
  //   firstElement <- findList.headOption
  //   resultText   <- firstElement.tryExtract(element(".result_text"))
  //   titleElement <- resultText.tryExtract(element("a"))
  //   title         = IMDBTitle(titleElement.text)
  //   resultTextStr = resultText.text
  //   year          = parseYear(resultTextStr)
  // } yield IMDBMovie(title, year)

  private def parseYear(resultTextStr: String): Option[ReleaseYear] = {
    val yearStartPos = resultTextStr.indexOf("(")
    if (yearStartPos > 0)
      Attempt
        .catchNonFatal(ReleaseYear(Year.parse(resultTextStr.substring(yearStartPos + 1, yearStartPos + 5))))
        .toOption
    else None
  }
}
