package pms.db

import pms.kernel._

trait KernelSkunkCodecs { self: codecs.type =>
  import self._
  implicit val varchar128_email: Codec[Email] = varchar(128).sproutRefined[Email]
}
