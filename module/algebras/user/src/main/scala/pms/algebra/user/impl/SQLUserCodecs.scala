package pms.algebra.user.impl

import pms._
import pms.algebra.user.UserRole
import pms.db._

object SQLUserCodecs extends SQLUserCodecs

trait SQLUserCodecs {
  import db.codecs._

  val enum_user_role: Codec[UserRole] =
    `enum`[UserRole](
      encode = (s: UserRole) => s.toName,
      decode = (s: String) => UserRole.fromName[Try](s).toOption,
      tpe    = skunk.data.Type("user_role"),
    )
}
