package pms

/**
  *
  * Simply an alias for busymachines.pureharm.json._ so that we don't have
  * to always import that as well
  *
  * @author Lorand Szakacs, https://github.com/lorandszakacs
  * @since 25 Jun 2018
  *
  */
package object json {
  type Codec[A] = io.circe.Codec[A]
  val Codec: io.circe.Codec.type = io.circe.Codec

  type Encoder[A] = io.circe.Encoder[A]
  val Encoder: io.circe.Encoder.type = io.circe.Encoder

  type Decoder[A] = io.circe.Decoder[A]
  val Decoder: io.circe.Decoder.type = io.circe.Decoder

  object implicits {}

  object derive {
    def codec[T]: Codec[T] = throw pms.core.Fail.nicata("json derivation")
  }
//  object implicits extends phjson.PureharmJsonImplicits with busymachines.pureharm.internals.json.AnomalyJsonCodec with JavaTimeJson with PMSCoreJson
//  object derive    extends phjson.SemiAutoDerivation

}
