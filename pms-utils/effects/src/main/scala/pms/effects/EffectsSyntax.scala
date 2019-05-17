package pms.effects

/**
  *
  * @author Lorand Szakacs, https://github.com/lorandszakacs
  * @since 08 May 2019
  *
  */
object EffectsSyntax {

  trait Implicits {
    implicit def transformFAIntoFAWithSyntax[F[_]: Concurrent, A](fa: F[A]): ConcurrentFAOps[F, A] =
      new ConcurrentFAOps(fa)
  }

  class ConcurrentFAOps[F[_], A](fa: F[A])(implicit F: Concurrent[F]) {
    import pms.effects.implicits._

    def forkAndForget: F[Unit] =
      F.start(fa).void
  }
}
