package pms.algebra.imdb

import net.ruippeixotog.scalascraper.model.Document
import pms.algebra.imdb.extra.RateLimiter
import pms.effects._

import scala.concurrent.duration._

/**
  *
  * @author Lorand Szakacs, https://github.com/lorandszakacs
  * @since 25 Jun 2018
  *
  */
trait ModuleIMDBAsync[F[_]] {
  implicit def async: Async[F]

  implicit def scheduler: Scheduler

  def imdbAlgebraConfig : IMDBAlgebraConfig

  def rateLimiter = new RateLimiter[Document](FiniteDuration(imdbAlgebraConfig.requestsInterval, MILLISECONDS), imdbAlgebraConfig.requestsNumber)

  def imdbAlgebra: IMDBAlgebra[F] = _imdbAlgebra

  private lazy val _imdbAlgebra: IMDBAlgebra[F] = new impl.AsyncIMDBAlgebraImpl[F](rateLimiter)
}


