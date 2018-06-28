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

  implicit val emailCirceCodec: Codec[Email] = Codec.instance(
    encode = Encoder.apply[String].contramap(email => email.plainTextEmail),
    decode = Decoder.apply[String].emap(s => Email(s).left.map(_.message))
  )

  implicit val plainTextPasswordCirceCodec: Codec[PlainTextPassword] = Codec.instance(
    encode = Encoder.apply[String].contramap(ptp => ptp.plainText),
    decode = Decoder.apply[String].emap(s => PlainTextPassword(s).left.map(_.message))
  )
}
