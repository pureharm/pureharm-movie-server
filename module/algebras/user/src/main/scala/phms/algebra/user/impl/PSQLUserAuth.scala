package phms.algebra.user.impl

import phms._
import phms.db._
import phms.algebra.user._
import phms.time._

object PSQLUserAuth {

  case class UserAuthRepr(
    token:     AuthenticationToken,
    userID:    UserID,
    expiresAt: UserAuthExpiration,
  )
  import phms.db.codecs._

  /*_*/
  private val token:      Column = const"token"
  private val user_id:    Column = const"user_id"
  private val expires_at: Column = const"expires_at"

  private val varchar64_token:        Codec[AuthenticationToken] = varchar(64).sprout[AuthenticationToken]
  private val uuid_user_id:           Codec[UserID]              = PSQLUserCodecs.uuid_user_id
  private val timestamptz_expires_at: Codec[UserAuthExpiration]  = timestamptz.sprout[UserAuthExpiration]

  private val user_auths_row:   Row       = sql"$token, $user_id, $expires_at"
  private val user_auths_table: TableName = const"user_authentications"

  private val user_auth_repr: Codec[UserAuthRepr] = (varchar64_token ~ uuid_user_id ~ timestamptz_expires_at).gimap

  /*_*/
}

final case class PSQLUserAuth[F[_]](private val session: Session[F])(implicit F: MonadCancelThrow[F], time: Time[F]) {
  import PSQLUserAuth._
  import phms.db.codecs._

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
           WHERE $token = $varchar64_token
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

  /*_*/
}
