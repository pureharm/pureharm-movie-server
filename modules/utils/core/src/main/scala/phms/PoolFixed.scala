/*
 * Copyright 2021 BusyMachines
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
