package phms.algebra.imdb

import phms.algebra.imdb.impl.IMDBAlgebraImpl
import phms._

/** @author Lorand Szakacs, https://github.com/lorandszakacs
  * @since 25 Jun 2018
  */
trait IMDBAlgebra[F[_]] {
  def scrapeMovieByTitle(title: TitleQuery): F[Option[IMDBMovie]]
}

object IMDBAlgebra {
  import net.ruippeixotog.scalascraper.browser.JsoupBrowser

  def resource[F[_]](throttler: EffectThrottler[F])(implicit F: Sync[F]): Resource[F, IMDBAlgebra[F]] =
    new IMDBAlgebraImpl[F](throttler, new JsoupBrowser()).pure[Resource[F, *]]

}
