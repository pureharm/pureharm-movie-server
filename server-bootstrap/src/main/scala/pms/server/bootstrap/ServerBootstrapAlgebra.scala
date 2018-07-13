package pms.server.bootstrap

import cats.implicits._

import pms.effects._
import pms.core._
import pms.algebra.user._

import io.chrisdavenport.log4cats.slf4j.Slf4jLogger

/**
  *
  * This should be used only in development, and testing!
  *
  * @author Lorand Szakacs, https://github.com/lorandszakacs
  * @since 13 Jul 2018
  *
  */
sealed abstract class ServerBootstrapAlgebra[F[_]](
  private val uca: UserAccountAlgebra[F],
  private val uba: UserAccountBootstrapAlgebra[F]
)(
  implicit val F: Sync[F]
) {

  private val logger = Slf4jLogger.unsafeCreate[F]

  final def bootStrapSuperAdmin(email: Email, pw: PlainTextPassword): F[User] =
    bootStrapUser(email, pw, UserRole.SuperAdmin)

  final def bootStrapUser(email: Email, pw: PlainTextPassword, role: UserRole): F[User] =
    this.bootStrapUser(UserRegistration(email, pw, role))

  final def bootStrapUser(reg: UserRegistration): F[User] =
    for {
      token <- uba.bootstrapUser(reg)
      user  <- uca.registrationStep2(token)
      _ <- logger.info(
            s"BOOTSTRAP — inserting user: role=${reg.role.productPrefix} email=${reg.email.plainTextEmail} pw=${reg.pw.plainText}"
          )
    } yield user
}

object ServerBootstrapAlgebra {

  def async[F[_]: Async](uca: UserAccountAlgebra[F], uba: UserAccountBootstrapAlgebra[F]): ServerBootstrapAlgebra[F] =
    new ServerBootstrapAlgebra(uca, uba) {}

}
