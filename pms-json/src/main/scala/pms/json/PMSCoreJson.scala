package pms.json

import pms.core._

/**
  *
  * @author Lorand Szakacs, https://github.com/lorandszakacs
  * @since 25 Jun 2018
  *
  */
object PMSCoreJson extends PMSCoreJson

trait PMSCoreJson {

  implicit val emailCodec: Codec[Email] = ???

  implicit val PlainTextPasswordCodec: Codec[PlainTextPassword] =
    ???
}
