package pms.algebra.imdb.impl

import pms.effects._
import pms.algebra.imdb._

/**
  *
  * @author Lorand Szakacs, https://github.com/lorandszakacs
  * @since 25 Jun 2018
  *
  */
final private[imdb] class AsyncIMDBAlgebraImpl[F[_]](
  implicit val F: Async[F]
) extends IMDBAlgebra[F] {

  override def scrapeMovieByTitle(title: TitleQuery): F[Option[IMDBMovie]] =
    F.delay(???)
}
