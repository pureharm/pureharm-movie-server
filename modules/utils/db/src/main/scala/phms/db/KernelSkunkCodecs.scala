package phms.db

import phms.kernel._

trait KernelSkunkCodecs { self: codecs.type =>
  implicit val varchar128_email: Codec[Email] = varchar(128).sproutRefined[Email]
}
