package pms.algebra

import java.time.Year

import pms._

/** @author Lorand Szakacs, https://github.com/lorandszakacs
  * @since 25 Jun 2018
  */
package object imdb {
  object TitleQuery extends SproutSub[String]
  type TitleQuery = TitleQuery.Type

  object IMDBTitle extends SproutSub[String]
  type IMDBTitle = IMDBTitle.Type

  object ReleaseYear extends SproutSub[Year]
  type ReleaseYear = ReleaseYear.Type
}
