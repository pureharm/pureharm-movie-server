package phms

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
    import io.circe.generic.codec.DerivedAsObjectCodec
    import shapeless.Lazy

    def codec[T](implicit codec: Lazy[DerivedAsObjectCodec[T]]): Codec[T] =
      io.circe.generic.semiauto.deriveCodec[T]
  }

  implicit def sproutJSONEncoder[O, N](implicit ot: OldType[O, N], enc: Encoder[O]): Encoder[N] =
    enc.contramap(ot.oldType)

  implicit def sproutNewtypeJSONDecoder[O, N](implicit nt: NewType[O, N], dec: Decoder[O]): Decoder[N] =
    dec.imap(nt.newType)(nt.oldType)

  implicit def sproutRefinedThrowJSONDecoder[O, N](implicit nt: RefinedTypeThrow[O, N], dec: Decoder[O]): Decoder[N] =
    dec.emapTry(o => nt.newType[Try](o))

}
