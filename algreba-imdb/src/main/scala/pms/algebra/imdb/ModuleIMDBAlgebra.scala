package pms.algebra.imdb

import cats.effect.Timer
import net.ruippeixotog.scalascraper.model.Document
import pms.algebra.imdb.extra.RateLimiter
import cats.implicits._
import pms.core.Module

/**
  *
  * @author Lorand Szakacs, https://github.com/lorandszakacs
  * @since 25 Jun 2018
  *
  */
trait ModuleIMDBAlgebra[F[_]] { this: Module[F] =>
  implicit def timer: Timer[F]

  def imdbAlgebraConfig: IMDBAlgebraConfig

  def imdbAlgebra: F[IMDBAlgebra[F]] = _imdbAlgebra

  private lazy val _imdbAlgebra: F[IMDBAlgebra[F]] = singleton {
    for {
      rl <- rateLimiter
    } yield new impl.AsyncIMDBAlgebraImpl[F](rl)
  }

  private lazy val rateLimiter: F[RateLimiter[F, Document]] = singleton {
    RateLimiter.async[F, Document](
      interval = imdbAlgebraConfig.requestsInterval,
      size     = imdbAlgebraConfig.requestsNumber
    )
  }

}
