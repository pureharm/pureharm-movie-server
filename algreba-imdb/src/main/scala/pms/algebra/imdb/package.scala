package pms.algebra

import java.time.Year

import pms.core._

/**
  *
  * @author Lorand Szakacs, https://github.com/lorandszakacs
  * @since 25 Jun 2018
  *
  */
package object imdb {
  object TitleQuery extends PhantomType[String]
  type TitleQuery = TitleQuery.Type

  object IMDBTitle extends PhantomType[String]
  type IMDBTitle = IMDBTitle.Type

  object ReleaseYear extends PhantomType[Year]
  type ReleaseYear = ReleaseYear.Type
}
