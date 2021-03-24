package phms.db

import fs2.io.net.Network
import phms._
import phms.db._
import phms.db.config._

object DBPool {

  def apply[F[_]](implicit sp: DBPool[F]): DBPool[F] = sp

  def resource[F[_]](
    config:     DBConnectionConfig
  )(implicit F: Concurrent[F], console: Console[F], network: Network[F]): Resource[F, Resource[F, Session[F]]] = {
    import natchez.Trace
    /** By setting the search path setting of the session we tell
      * postgresql which schema to use.
      *
      * See this gitter discussion:
      * https://gitter.im/skunk-pg/Lobby?at=6025643b84e66b7f7ee82968
      *
      * And the postgresql specs:
      * https://www.postgresql.org/docs/current/runtime-config-client.html
      */
    val parameters:         Map[String, String] =
      Session.DefaultConnectionParameters.updated("search_path", SchemaName.oldType(config.schema))
    implicit val noopTrace: Trace[F]            = Trace.Implicits.noop[F]
    import skunk.Strategy
    Session
      .pooled(
        host       = config.host.toString,
        port       = config.port.value,
        user       = config.username,
        database   = config.dbName,
        password   = Option(config.password),
        debug      = false,
        max        = 64,
        parameters = parameters,
        //required for figuring out user defined types
        strategy   = Strategy.SearchPath,
        //debug      = true,
      )

  }
}
