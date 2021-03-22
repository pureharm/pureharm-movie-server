package phms.algebra.user.impl

import phms._
import phms.algebra.user._
import phms.db._

object PSQLUserCodecs extends PSQLUserCodecs

trait PSQLUserCodecs {
  import db.codecs._

  val uuid_user_id: Codec[UserID] = uuid.sprout[UserID]

  val enum_user_role: Codec[UserRole] =
    `enum`[UserRole](
      encode = (s: UserRole) => s.toName,
      decode = (s: String) => UserRole.fromName[Try](s).toOption,
      tpe    = skunk.data.Type("user_role"),
    )
}
