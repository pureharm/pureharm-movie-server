package pms

/** Simply an alias for busymachines.pureharm.json._ so that we don't have
  * to always import that as well
  *
  * @author Lorand Szakacs, https://github.com/lorandszakacs
  * @since 25 Jun 2018
  */
package object json extends JavaTimeJson {
  type Codec[A] = io.circe.Codec[A]
  val Codec: io.circe.Codec.type = io.circe.Codec

  type Encoder[A] = io.circe.Encoder[A]
  val Encoder: io.circe.Encoder.type = io.circe.Encoder

  type Decoder[A] = io.circe.Decoder[A]
  val Decoder: io.circe.Decoder.type = io.circe.Decoder

  object derive {
    def codec[T]: Codec[T] = throw pms.Fail.nicata("json derivation")
  }

  implicit def sproutJSONEncoder[O, N](implicit enc: Encoder[O], ot: OldType[O, N]): Encoder[N] =
    enc.contramap(ot.oldType)

  implicit def sproutNewtypeJSONDecoder[O, N](implicit dec: Decoder[O], nt: NewType[O, N]): Decoder[N] =
    dec.imap(nt.newType)(nt.oldType)

  implicit def sproutRefinedThrowJSONDecoder[O, N](implicit dec: Decoder[O], nt: RefinedTypeThrow[O, N]): Decoder[N] =
    dec.emap(o => nt.newType[Attempt](o).leftMap(_.getMessage))

//  object implicits extends phjson.PureharmJsonImplicits with busymachines.pureharm.internals.json.AnomalyJsonCodec with JavaTimeJson with PMSCoreJson
//  object derive    extends phjson.SemiAutoDerivation

}
