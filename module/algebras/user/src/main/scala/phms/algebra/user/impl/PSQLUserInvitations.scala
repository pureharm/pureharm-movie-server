package phms.algebra.user.impl

import phms._
import phms.kernel._
import phms.db._
import phms.algebra.user._

object PSQLUserInvitations {

  final case class UserInvitationRepr(
    email:           Email,
    role:            UserRole,
    invitationToken: UserInviteToken,
    expiresAt:       UserInviteExpiration,
  )
  import phms.db.codecs._

  /*_*/
  private val email:            Column = const"email"
  private val role:             Column = const"role"
  private val invitation_token: Column = const"invitation_token"
  private val expires_at:       Column = const"expires_at"

  private val user_invitations_row:   Row       = sql"$email, $role, $invitation_token, $expires_at"
  private val user_invitations_table: TableName = const"user_invitations"

  private val enum_role: Codec[UserRole] = PSQLUserCodecs.enum_user_role

  private val varchar96_invitation_token: Codec[UserInviteToken]      =
    PSQLUserCodecs.varchar96_token.sprout[UserInviteToken]
  private val timestamptz_expires_at:     Codec[UserInviteExpiration] = timestamptz.sprout

  private val user_invitation_repr: Codec[UserInvitationRepr] =
    (varchar128_email ~ enum_role ~ varchar96_invitation_token ~ timestamptz_expires_at).gimap
  /*_*/
}

final case class PSQLUserInvitations[F[_]](private val session: Session[F])(implicit F: MonadCancelThrow[F]) {
  import PSQLUserInvitations._
  import phms.db.codecs._
  /*_*/

  //TODO: do not void result, and check for completion, and conflict
  def insert(toInsert: UserInvitationRepr): F[Unit] =
    session
      .prepare(
        sql"""
          INSERT into $user_invitations_table ($user_invitations_row)
          VALUES ${user_invitation_repr.values}
         """.command: Command[UserInvitationRepr]
      )
      .use(pc => pc.execute(toInsert).void)

  def findByInvite(t: UserInviteToken): F[Option[UserInvitationRepr]] =
    session
      .prepare(
        sql"""
           SELECT $user_invitations_row 
           FROM $user_invitations_table
           WHERE $invitation_token = $varchar96_invitation_token
         """.query(user_invitation_repr): Query[UserInviteToken, UserInvitationRepr]
      )
      .use(pc => pc.option(t))

  def findByEmail(t: Email): F[Option[UserInvitationRepr]] =
    session
      .prepare(
        sql"""
           SELECT $user_invitations_row
           FROM $user_invitations_table
           WHERE $email = $varchar128_email
         """.query(user_invitation_repr): Query[Email, UserInvitationRepr]
      )
      .use(pc => pc.option(t))

  def deleteByInvite(toDelete: UserInviteToken): F[Unit] =
    session
      .prepare(
        sql"""
          DELETE FROM $user_invitations_table
          WHERE $invitation_token = $varchar96_invitation_token
         """.command: Command[UserInviteToken]
      )
      .use(_.execute(toDelete).void)

  /*_*/
}
