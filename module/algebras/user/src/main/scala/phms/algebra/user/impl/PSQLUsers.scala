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

  private val uuid_user_id:                   Codec[UserID]              = uuid.sprout[UserID]
  private val enum_role:                      Codec[UserRole]            = PSQLUserCodecs.enum_user_role
  private val bytea_bcrypt:                   Codec[UserCrypto.BcryptPW] = bytea.sproutRefined
  private val varchar64_password_reset_token: Codec[PasswordResetToken]  = varchar(64).sprout

  private val users_repr_row: Row       = sql"$id, $email, $role $bcrypt_password_hash $password_reset_token"
  private val users_table:    TableName = const"users"

  private val user_repr: Codec[UserRepr] =
    (uuid_user_id ~ varchar128_email ~ enum_role ~ bytea_bcrypt ~ varchar64_password_reset_token.opt).gimap

  /*_*/
}

final case class PSQLUsers[F[_]](private val session: Session[F])(implicit F: MonadCancelThrow[F]) {
  import PSQLUsers._
  import phms.db.codecs._

  /*_*/
  def insert(toInsert:   UserRepr): F[Unit] = session
    .prepare(
      sql"""
          INSERT into $users_table ($users_repr_row)
          VALUES ${user_repr.values}
         """.command
    )
    .use((pc: PreparedCommand[F, UserRepr]) => pc.execute(toInsert).void)

  def updateRole(target: UserID, newRole: UserRole): F[Unit] = session
    .prepare(
      sql"""
         UPDATE $users_table
         SET $role=$enum_role
         WHERE $id=$uuid_user_id
       """.command
    )
    .use(_.execute(newRole ~ target).void)

  def findByID(target: UserID): F[Option[UserRepr]] =
    session
      .prepare(
        sql"""
           SELECT $users_repr_row
           FROM $users_table
           WHERE $id = $uuid_user_id
         """.query(user_repr)
      )
      .use(_.option(target))

  def findByEmail(target: Email): F[Option[UserRepr]] =
    session
      .prepare(
        sql"""
           SELECT $users_repr_row
           FROM $users_table
           WHERE $email = $varchar128_email
         """.query(user_repr)
      )
      .use(_.option(target))

  def findByPWReset(target: PasswordResetToken): F[Option[UserRepr]] =
    session
      .prepare(
        sql"""
           SELECT $users_repr_row
           FROM $users_table
           WHERE $password_reset_token = $varchar64_password_reset_token
         """.query(user_repr)
      )
      .use(_.option(target))

  def setPasswordReset(target: Email, token: PasswordResetToken): F[Unit] =
    session
      .prepare(
        sql"""
         UPDATE $users_table
         SET $password_reset_token=$varchar64_password_reset_token
         WHERE $email=$varchar128_email
       """.command
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
         SET $password_reset_token=${varchar64_password_reset_token.opt},
             $bcrypt_password_hash=$bytea_bcrypt
         WHERE $password_reset_token=$varchar64_password_reset_token
       """.command
      )
      .use(_.execute(Option.empty[PasswordResetToken] ~ newPW ~ target).void)
  /*_*/
}
