package pms.core

import java.util.concurrent.{ExecutorService, ThreadFactory}

private[core] object PoolUtil {

  def unsafeAvailableCPUs: Int = Runtime.getRuntime.availableProcessors()

  private[core] def exitOnFatal(ec:                 ExecutorService): ExecutionContext = new ExecutionContext {
    private val underlying: ExecutionContext = ExecutionContext.fromExecutorService(ec)

    override def execute(r: Runnable): Unit = {
      underlying.execute(new Runnable {
        def run(): Unit = {
          try {
            r.run()
          }
          catch {
            case NonFatal(t) =>
              reportFailure(t)

            case t: Throwable =>
              // under most circumstances, this will work even with fatal errors
              t.printStackTrace()
              System.exit(1)
          }
        }
      })
    }

    override def reportFailure(t: Throwable): Unit =
      underlying.reportFailure(t)
  }

  /** @param prefix
    *   A thread factory where the name + daemon status is prefixed to the thread ID.
    * @return
    */
  private[core] def namedThreadPoolFactory(prefix: String, daemonThreads: Boolean): ThreadFactory = new ThreadFactory {

    override def newThread(r: Runnable): Thread = {
      val thread = new Thread(r)
      if (daemonThreads) {
        thread.setName(s"$prefix-$Daemon-${thread.getId}")
      }
      else {
        thread.setName(s"$prefix-${thread.getId}")
      }
      thread.setDaemon(daemonThreads)
      thread
    }
  }

  private val Daemon = "daemon"
}
