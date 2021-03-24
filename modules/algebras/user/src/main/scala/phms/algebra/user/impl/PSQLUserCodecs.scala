package phms.algebra.user.impl

import phms._
import phms.algebra.user._
import phms.db._

object PSQLUserCodecs extends PSQLUserCodecs

trait PSQLUserCodecs {
  import db.codecs._

  //all our tokens are base64 encodings of 64 bytes ~ 4*(64/3) bytes.
  val varchar96_token: Codec[String] = varchar(96)
  val uuid_user_id:    Codec[UserID] = uuid.sprout[UserID]

  val enum_user_role: Codec[UserRole] =
    `enum`[UserRole](
      encode = (s: UserRole) => s.toName,
      decode = (s: String) => UserRole.fromName[Try](s).toOption,
      tpe    = skunk.data.Type("user_role"),
    )
}
