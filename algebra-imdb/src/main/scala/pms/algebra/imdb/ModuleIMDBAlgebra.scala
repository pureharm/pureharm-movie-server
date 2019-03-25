package pms.algebra.imdb

import cats.effect.Timer
import net.ruippeixotog.scalascraper.model.Document
import cats.implicits._
import pms.core.Module
import pms.effects.EffectThrottler

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
      rl <- effectThrottler
    } yield new impl.AsyncIMDBAlgebraImpl[F](rl)
  }

  private lazy val effectThrottler: F[EffectThrottler[F, Document]] = singleton {
    EffectThrottler.concurrent[F, Document](
      interval = imdbAlgebraConfig.requestsInterval,
      amount   = imdbAlgebraConfig.requestsNumber
    )
  }

}
