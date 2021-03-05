package pms.algebra.user.impl

import pms.core._

import scala.concurrent.duration._

//import tsec.jwt._
//import tsec.jws.mac._
//
//import tsec.mac.jca._
//import tsec.passwordhashers._
//import tsec.passwordhashers.jca._

import pms.core.PlainTextPassword

/**
  *
  * @author Lorand Szakacs, https://github.com/lorandszakacs
  * @since 22 Mar 2019
  *
  */
private[impl] object UserCrypto {

  //FIXME: reimplement tsec
  type BcryptPW = String
  def BcryptPW(pt: String): BcryptPW = pt

  //FIXME: modify this to be able to tell it to generate one of the three specific tokens
  /**
    * Ideally, I'd want to write something like this:
    * {{{
    *   UserCrypto.generateToken[F, AuthenticationToken]
    * }}}
    *
    */
  private[impl] def generateToken[F[_]: Random]: F[String] = Fail.nicata("generateToken").raiseError[F, String]

  private[impl] def hashPWWithBcrypt[F[_]: Sync](ptpw: PlainTextPassword): F[BcryptPW] =
    Fail.nicata(s"hashPWWithBcrypt").raiseError[F, BcryptPW]

  private[impl] def checkUserPassword[F[_]: Sync](p: String, hash: UserCrypto.BcryptPW): F[Boolean] =
    Fail.nicata(s"checkUserPassword $p $hash").raiseError[F, Boolean]
}
