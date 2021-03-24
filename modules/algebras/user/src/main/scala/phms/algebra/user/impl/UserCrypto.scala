package phms.algebra.user.impl

import phms._
import phms.kernel._

/** @author Lorand Szakacs, https://github.com/lorandszakacs
  * @since 22 Mar 2019
  */
private[impl] object UserCrypto {

  type BcryptPW = phms.crypto.BCryptHash

  private[impl] def generateToken[F[_], TokenType](implicit
    F:  ApplicativeThrow[F],
    sr: SecureRandom[F],
    nt: NewType[String, TokenType],
  ): F[TokenType] =
    sr.nextBytesAsBase64(64).map(nt.newType)

  private[impl] def hashPWWithBcrypt[F[_]](
    ptpw:       PlainTextPassword
  )(implicit F: MonadThrow[F], sr: SecureRandom[F]): F[BcryptPW] = phms.crypto.BCrypt.createBCrypt[F](ptpw)

  private[impl] def checkUserPassword[F[_]](
    p:          PlainTextPassword,
    hash:       UserCrypto.BcryptPW,
  )(implicit F: MonadThrow[F], sr: SecureRandom[F]): F[Boolean] = phms.crypto.BCrypt.verify[F](p, hash)
}
