package pms.algebra.user.impl

import pms.effects._
import pms.effects.implicits._

import scala.concurrent.duration._

import tsec.jwt._
import tsec.jws.mac._

import tsec.mac.jca._
import tsec.passwordhashers._
import tsec.passwordhashers.jca._

import pms.core.PlainTextPassword

/**
  *
  * @author Lorand Szakacs, https://github.com/lorandszakacs
  * @since 22 Mar 2019
  *
  */
private[impl] object UserCrypto {

  type BcryptPW = PasswordHash[BCrypt]
  def BcryptPW(pt: String): BcryptPW = PasswordHash[BCrypt](pt)

  //FIXME: modify this to be able to tell it to generate one of the three specific tokens
  /**
    * Ideally, I'd want to write something like this:
    * {{{
    *   UserCrypto.generateToken[F, AuthenticationToken]
    * }}}
    *
    */
  private[impl] def generateToken[F[_]: Sync]: F[String] =
    for {
      key <- HMACSHA256.generateKey[F]
      claims <- JWTClaims.withDuration[F](expiration = Some(10.minutes)) //FIXME: make this configurable
      token <- JWTMac.buildToString[F, HMACSHA256](claims, key)
    } yield token

  private[impl] def hashPWWithBcrypt[F[_]: Sync](
      ptpw: PlainTextPassword): F[BcryptPW] =
    BCrypt.hashpw[F](ptpw.plainText)

  private[impl] def checkUserPassword[F[_]: Sync](
      p: String,
      hash: UserCrypto.BcryptPW): F[Boolean] = {
    BCrypt.checkpw[F](p, hash).map {
      case tsec.common.Verified           => true
      case tsec.common.VerificationFailed => false
    }
  }
}
