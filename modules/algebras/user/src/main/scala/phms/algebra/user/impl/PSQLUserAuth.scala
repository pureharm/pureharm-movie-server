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

package phms.algebra.user.impl

import phms.*
import phms.db.*
import phms.algebra.user.*

object PSQLUserAuth {

  case class UserAuthRepr(
    token:     AuthenticationToken,
    userID:    UserID,
    expiresAt: UserAuthExpiration,
  )
  import phms.db.codecs.*

  /*_*/
  private val token:      Column = const"token"
  private val user_id:    Column = const"user_id"
  private val expires_at: Column = const"expires_at"

  private val varchar96_token:        Codec[AuthenticationToken] = PSQLUserCodecs.varchar96_token.sprout[AuthenticationToken]
  private val uuid_user_id:           Codec[UserID]              = PSQLUserCodecs.uuid_user_id
  private val timestamptz_expires_at: Codec[UserAuthExpiration]  = timestamptz.sprout[UserAuthExpiration]

  private val user_auths_row:   Row       = sql"$token, $user_id, $expires_at"
  private val user_auths_table: TableName = const"user_authentications"

  private val user_auth_repr: Codec[UserAuthRepr] = (varchar96_token ~ uuid_user_id ~ timestamptz_expires_at).gimap
  /*_*/
}

final case class PSQLUserAuth[F[_]](private val session: Session[F])(using F: MonadCancelThrow[F]) {
  import PSQLUserAuth.*

  /*_*/
  def insert(toInsert: UserAuthRepr): F[Unit] = session
    .prepare(
      sql"""
          INSERT into $user_auths_table ($user_auths_row)
          VALUES ${user_auth_repr.values}
         """.command: Command[UserAuthRepr]
    )
    .use(pc => pc.execute(toInsert).void)

  def findForToken(t: AuthenticationToken): F[Option[UserAuthRepr]] =
    session
      .prepare(
        sql"""
           SELECT $user_auths_row 
           FROM $user_auths_table
           WHERE $token = $varchar96_token
         """.query(user_auth_repr): Query[AuthenticationToken, UserAuthRepr]
      )
      .use(pc => pc.option(t))

  def deleteAllForUser(userID: UserID): F[Unit] = session
    .prepare(
      sql"""
          DELETE FROM $user_auths_table
          WHERE $user_id = $uuid_user_id
         """.command: Command[UserID]
    )
    .use(pc => pc.execute(userID).void)

  def deleteToken(t: AuthenticationToken): F[Unit] = session
    .prepare(
      sql"""
          DELETE FROM $user_auths_table
          WHERE $token = $varchar96_token
         """.command: Command[AuthenticationToken]
    )
    .use(pc => pc.execute(t).void)

  /*_*/
}
