package pms.server.bootstrap

import pms.effects._
import pms.effects.implicits._
import pms.core._
import pms.algebra.user._

import pms.logger._

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
  private val uba: UserAccountBootstrapAlgebra[F],
)(
  implicit val F: Sync[F],
) {

  private val logger = PMSLogger.getLogger[F]

  final def bootStrapSuperAdmin(email: Email, pw: PlainTextPassword): F[User] =
    bootStrapUser(email, pw, UserRole.SuperAdmin)

  final def bootStrapUser(email: Email, pw: PlainTextPassword, role: UserRole): F[User] =
    this.bootStrapUser(UserInvitation(email, role), pw)

  final def bootStrapUser(inv: UserInvitation, pw: PlainTextPassword): F[User] =
    for {
      token <- uba.bootstrapUser(inv)
      user  <- uca.registrationStep2(token, pw)
      _ <- logger.info(
        s"BOOTSTRAP — inserting user: role=${inv.role.productPrefix} email=${inv.email.plainTextEmail} pw=${pw.plainText}",
      )
    } yield user
}

object ServerBootstrapAlgebra {

  def async[F[_]: Async](uca: UserAccountAlgebra[F], uba: UserAccountBootstrapAlgebra[F]): ServerBootstrapAlgebra[F] =
    new ServerBootstrapAlgebra(uca, uba) {}

}
