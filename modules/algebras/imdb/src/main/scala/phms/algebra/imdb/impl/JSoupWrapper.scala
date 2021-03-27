package phms.algebra.imdb.impl

import phms.{Resource, Sync}
import net.ruippeixotog.scalascraper.browser.JsoupBrowser
import net.ruippeixotog.scalascraper.model.Document

sealed trait JsoupWrapper[F[_]] {
  def getDocument(url: String): F[Document]
}

object JsoupWrapper {

  def resource[F[_]](implicit F: Sync[F]): Resource[F, JsoupWrapper[F]] =
    for {
      browser <- Resource.eval[F, JsoupBrowser](jsoupBrowser[F])
    } yield new JsoupWrapper[F] {

      override def getDocument(url: String): F[Document] =
        F.blocking[Document](browser.get(url))
    }

  def jsoupBrowser[F[_]](implicit F: Sync[F]): F[JsoupBrowser] = F.blocking(new JsoupBrowser())

}
