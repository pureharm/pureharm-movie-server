package phms

package object db extends skunk.syntax.ToAllOps {
  type Session[F[_]] = skunk.Session[F]
  val Session: skunk.Session.type = skunk.Session

  type DBPool[F[_]] = Resource[F, Session[F]]

  type Codec[A]   = skunk.Codec[A]
  type Encoder[A] = skunk.Encoder[A]
  type Decoder[A] = skunk.Decoder[A]

  val Codec:   skunk.Codec.type   = skunk.Codec
  val Encoder: skunk.Encoder.type = skunk.Encoder
  val Decoder: skunk.Decoder.type = skunk.Decoder

  type Void = skunk.Void
  val Void: skunk.Void.type = skunk.Void

  type Command[A]  = skunk.Command[A]
  type Query[I, O] = skunk.Query[I, O]

  type ~[+A, +B] = skunk.~[A, B]

  type PreparedQuery[F[_], I, O] = skunk.PreparedQuery[F, I, O]
  val PreparedQuery: skunk.PreparedQuery.type = skunk.PreparedQuery

  type PreparedCommand[F[_], A] = skunk.PreparedCommand[F, A]
  val PreparedCommand: skunk.PreparedCommand.type = skunk.PreparedCommand

  type Column    = skunk.Fragment[skunk.Void]
  type TableName = skunk.Fragment[skunk.Void]
  type Row       = skunk.Fragment[skunk.Void]
}
