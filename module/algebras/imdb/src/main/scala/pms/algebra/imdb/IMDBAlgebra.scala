package pms.algebra.imdb

import cats.effect.{Async, Resource}
import net.ruippeixotog.scalascraper.browser.JsoupBrowser
import pms.algebra.imdb.impl.IMDBAlgebraImpl
import pms._

/** @author Lorand Szakacs, https://github.com/lorandszakacs
  * @since 25 Jun 2018
  */
trait IMDBAlgebra[F[_]] {
  def scrapeMovieByTitle(title: TitleQuery): F[Option[IMDBMovie]]
}

object IMDBAlgebra {

  def resource[F[_]: Async](throttler: EffectThrottler[F]): Resource[F, IMDBAlgebra[F]] =
    new IMDBAlgebraImpl[F](throttler, new JsoupBrowser()).pure[Resource[F, *]]

}
