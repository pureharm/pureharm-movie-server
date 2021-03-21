package pms

package object db {
  type Session[F[_]] = skunk.Session[F]
  val Session: skunk.Session.type = skunk.Session
}
