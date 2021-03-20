package pms.algebra.user.impl

import pms._
import pms.kernel._
import pms.algebra.user._

/** @author Lorand Szakacs, https://github.com/lorandszakacs
  * @since 22 Mar 2019
  */
private[impl] object UserCrypto {

  type BcryptPW = pms.crypto.BCryptHash

  private[impl] def generateToken[F[_], TokenType](implicit
    F:  ApplicativeThrow[F],
    sr: SecureRandom[F],
    nt: NewType[String, TokenType],
  ): F[TokenType] =
    sr.nextString(64).map(nt.newType)

  private[impl] def hashPWWithBcrypt[F[_]](
    ptpw:       PlainTextPassword
  )(implicit F: MonadThrow[F], sr: SecureRandom[F]): F[BcryptPW] = pms.crypto.BCrypt.createBCrypt[F](ptpw)

  private[impl] def checkUserPassword[F[_]](
    p:          PlainTextPassword,
    hash:       UserCrypto.BcryptPW,
  )(implicit F: MonadThrow[F], sr: SecureRandom[F]): F[Boolean] = pms.crypto.BCrypt.verify[F](p, hash)
}
