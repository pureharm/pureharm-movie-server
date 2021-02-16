package pms.core

import pms.core
import pms.effects._

/**
  *
  * @author Lorand Szakacs, https://github.com/lorandszakacs
  * @since 21 Mar 2019
  *
  */
trait Module[F[_]] {
  implicit def F: Concurrent[F]

  //FIXME: this is broken, does not work properly.
  final protected def singleton[T](f: F[T]): F[T] = Module.memoize(f)

  implicit final def concurrentModuleOps[T](fa: F[T]): Module.ModuleOps[F, T] =
    new core.Module.ModuleOps[F, T](fa)
}

object Module {

  class ModuleOps[F[_], T](val fa: F[T]) extends AnyVal {
    def covary[Y >: T]: F[Y] = fa.asInstanceOf[F[Y]]
    def memoize(implicit F: Concurrent[F]): F[T] = Module.memoize(fa)

  }

  protected def memoize[F[_]: Concurrent, T](f: F[T]): F[T] =
    Concurrent.apply[F].flatten(Concurrent.memoize(f)(Concurrent.apply[F]))
}
