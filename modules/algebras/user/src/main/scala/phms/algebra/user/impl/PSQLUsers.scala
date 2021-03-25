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

import phms._
import phms.kernel._
import phms.db._
import phms.algebra.user._

object PSQLUsers {

  case class UserRepr(
    id:           UserID,
    email:        Email,
    role:         UserRole,
    bcryptPW:     UserCrypto.BcryptPW,
    pwResetToken: Option[PasswordResetToken],
  )
  import phms.db.codecs._

  /*_*/
  private val id:                   Column = const"id"
  private val email:                Column = const"email"
  private val role:                 Column = const"role"
  private val bcrypt_password_hash: Column = const"bcrypt_password_hash"
  private val password_reset_token: Column = const"password_reset_token"

  private val uuid_user_id:                   Codec[UserID]              = PSQLUserCodecs.uuid_user_id
  private val enum_role:                      Codec[UserRole]            = PSQLUserCodecs.enum_user_role
  private val bytea_bcrypt:                   Codec[UserCrypto.BcryptPW] = bytea.sproutRefined
  private val varchar96_password_reset_token: Codec[PasswordResetToken]  = PSQLUserCodecs.varchar96_token.sprout

  private val users_repr_row: Row       = sql"$id, $email, $role, $bcrypt_password_hash, $password_reset_token"
  private val users_table:    TableName = const"users"

  private val user_repr: Codec[UserRepr] =
    (uuid_user_id ~ varchar128_email ~ enum_role ~ bytea_bcrypt ~ varchar96_password_reset_token.opt).gimap

  /*_*/
}

final case class PSQLUsers[F[_]](private val session: Session[F])(implicit F: MonadCancelThrow[F]) {
  import PSQLUsers._
  import phms.db.codecs._

  /*_*/
  def insert(toInsert: UserRepr): F[Unit] = session
    .prepare(
      sql"""
          INSERT INTO $users_table ($users_repr_row)
          VALUES ${user_repr.values}
         """.command: Command[UserRepr]
    )
    .use(pc => pc.execute(toInsert).void)

  def updateRole(target: UserID, newRole: UserRole): F[Unit] = session
    .prepare(
      sql"""
         UPDATE $users_table
         SET $role=$enum_role
         WHERE $id=$uuid_user_id
       """.command: Command[UserRole ~ UserID]
    )
    .use(_.execute(newRole ~ target).void)

  def findByID(target: UserID): F[Option[UserRepr]] =
    session
      .prepare(
        sql"""
           SELECT $users_repr_row
           FROM $users_table
           WHERE $id = $uuid_user_id
         """.query(user_repr): Query[UserID, UserRepr]
      )
      .use(_.option(target))

  def findByEmail(target: Email): F[Option[UserRepr]] =
    session
      .prepare(
        sql"""
           SELECT $users_repr_row
           FROM $users_table
           WHERE $email = $varchar128_email
         """.query(user_repr): Query[Email, UserRepr]
      )
      .use(_.option(target))

  def findByPWReset(target: PasswordResetToken): F[Option[UserRepr]] =
    session
      .prepare(
        sql"""
           SELECT $users_repr_row
           FROM $users_table
           WHERE $password_reset_token = $varchar96_password_reset_token
         """.query(user_repr): Query[PasswordResetToken, UserRepr]
      )
      .use(_.option(target))

  def setPasswordReset(target: Email, token: PasswordResetToken): F[Unit] =
    session
      .prepare(
        sql"""
         UPDATE $users_table
         SET $password_reset_token=$varchar96_password_reset_token
         WHERE $email=$varchar128_email
       """.command: Command[PasswordResetToken ~ Email]
      )
      .use(_.execute(token ~ target).void)

  /** We only reset the password of a user if the
    * given reset token matches. And we set the
    * reset token to NULL
    */
  def resetPassword(target: PasswordResetToken, newPW: UserCrypto.BcryptPW): F[Unit] =
    session
      .prepare(
        sql"""
         UPDATE $users_table
         SET $password_reset_token=${varchar96_password_reset_token.opt},
             $bcrypt_password_hash=$bytea_bcrypt
         WHERE $password_reset_token=$varchar96_password_reset_token
       """.command: Command[Option[PasswordResetToken] ~ UserCrypto.BcryptPW ~ PasswordResetToken]
      )
      .use(_.execute(Option.empty[PasswordResetToken] ~ newPW ~ target).void)
  /*_*/
}
