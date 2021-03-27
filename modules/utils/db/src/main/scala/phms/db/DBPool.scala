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

package phms.db

import fs2.io.net.Network
import phms.*
import phms.db.*
import phms.db.config.*

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
