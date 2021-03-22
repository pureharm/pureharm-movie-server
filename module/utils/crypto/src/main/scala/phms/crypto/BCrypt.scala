package phms.crypto

import phms._
import phms.kernel.PlainTextPassword

object BCrypt {
  import at.favre.lib.crypto.{bcrypt => jbcrypt}

  private val version:   jbcrypt.BCrypt.Version  = jbcrypt.BCrypt.Version.VERSION_2A
  private val hasher:    jbcrypt.BCrypt.Hasher   = jbcrypt.BCrypt.`with`(version)
  private val formatter: jbcrypt.BCryptFormatter = version.formatter
  private val verifier:  jbcrypt.BCrypt.Verifyer = jbcrypt.BCrypt.verifyer(version)

  private val cost_factor: Int = 4 //between 4 and 31

  def createBCrypt[F[_]](ptp: PlainTextPassword)(implicit F: MonadThrow[F], sr: SecureRandom[F]): F[BCryptHash] = for {
    //by providing the salt explicitly we ensure that we use our SecureRandom generator, instead of the library's
    saltBytes <- sr.nextBytes(16)
    pwBytes = ptp.utf8Bytes
    hashData <- sr.sync.blocking(hasher.hashRaw(cost_factor, saltBytes, pwBytes))
    hashBytes = formatter.createHashMessage(hashData)
    bcryptHash <- BCryptHash[F](hashBytes)
  } yield bcryptHash

  def verify[F[_]](password: PlainTextPassword, hash: BCryptHash)(implicit
    F:                       MonadThrow[F],
    sr:                      SecureRandom[F],
  ): F[Boolean] = for {
    result   <- sr.sync.blocking(verifier.verifyStrict(password.utf8Bytes, BCryptHash.oldType(hash)))
    verified <-
      if (!result.validFormat)
        Fail
          .iscata(
            "We have a bug, our BCrypt hash has invalid format, and we are fully responsible for its creation",
            "Bcrypt.verify",
          )
          .raiseError[F, Boolean]
      else result.verified.pure[F]
  } yield verified

}
