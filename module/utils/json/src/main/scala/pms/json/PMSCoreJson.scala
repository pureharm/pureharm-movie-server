package pms.json

import pms._

/**
  *
  * @author Lorand Szakacs, https://github.com/lorandszakacs
  * @since 25 Jun 2018
  *
  */
trait PMSCoreJson {

  implicit val emailCirceCodec: Codec[Email] = Codec.from(
    Decoder.apply[String].emap(s => Email(s).left.map(_.getMessage)),
    Encoder.apply[String].contramap(email => email.plainTextEmail),
  )

  implicit val plainTextPasswordCirceCodec: Codec[PlainTextPassword] =
    Codec.from(
      Decoder
        .apply[String]
        .emap(s => PlainTextPassword(s).left.map(_.getMessage)),
      Encoder.apply[String].contramap(ptp => ptp.plainText),
    )
}
