package phms

import cats.effect.{Resource, Sync}

import java.util.concurrent.{ExecutorService, Executors}

object PoolFixed {

  def fixed[F[_]: Sync](
    threadNamePrefix: String,
    maxThreads:       Int,
    daemons:          Boolean,
  ): Resource[F, ExecutionContext] = {
    val alloc = Sync[F].delay(unsafeExecutorService(threadNamePrefix, maxThreads, daemons))
    val free: ExecutorService => F[Unit] = (es: ExecutorService) => Sync[F].delay(es.shutdown())
    Resource.make(alloc)(free).map(es => PoolUtil.exitOnFatal(es))
  }

  private def unsafeExecutorService(threadNamePrefix: String, maxThreads: Int, daemons: Boolean): ExecutorService = {
    val bound  = math.max(1, maxThreads)
    val prefix = s"$threadNamePrefix-tc$maxThreads"
    Executors.newFixedThreadPool(bound, PoolUtil.namedThreadPoolFactory(prefix, daemons))
  }
}
