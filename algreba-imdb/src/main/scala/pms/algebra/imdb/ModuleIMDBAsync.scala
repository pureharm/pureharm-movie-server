package pms.algebra.imdb

import cats.effect.Timer
import net.ruippeixotog.scalascraper.model.Document
import pms.algebra.imdb.extra.RateLimiter
import pms.effects._
import cats.implicits._

import scala.concurrent.duration._

/**
  *
  * @author Lorand Szakacs, https://github.com/lorandszakacs
  * @since 25 Jun 2018
  *
  */
trait ModuleIMDBAsync[F[_]] {
  implicit def concurrent: Concurrent[F]
  implicit def timer:      Timer[F]

  def imdbAlgebraConfig: IMDBAlgebraConfig

  private[imdb] def rateLimiter: F[RateLimiter[F, Document]] = RateLimiter.async[F, Document](
    interval = FiniteDuration(imdbAlgebraConfig.requestsInterval, MILLISECONDS),
    size     = imdbAlgebraConfig.requestsNumber
  )

  def imdbAlgebra: F[IMDBAlgebra[F]] =
    for {
      rl <- rateLimiter
    } yield new impl.AsyncIMDBAlgebraImpl[F](rl)
}
