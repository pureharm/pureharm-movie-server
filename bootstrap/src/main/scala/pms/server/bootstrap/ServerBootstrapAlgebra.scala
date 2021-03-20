package pms.server.bootstrap

import pms._

import pms.algebra.user._
import pms.{Email, PlainTextPassword}
import pms.logger._

/**
  *
  * This should be used only in development, and testing!
  *
  * @author Lorand Szakacs, https://github.com/lorandszakacs
  * @since 13 Jul 2018
  *
  */
sealed abstract class ServerBootstrapAlgebra[F[_]: Sync](
  private val uca: UserAccountAlgebra[F],
  private val uba: UserAccountBootstrapAlgebra[F],
) {

  private val logger = Logger.getLogger[F]

  final def bootStrapSuperAdmin(email: Email, pw: PlainTextPassword): F[User] =
    bootStrapUser(email, pw, UserRole.SuperAdmin)

  final def bootStrapUser(email: Email, pw: PlainTextPassword, role: UserRole): F[User] =
    this.bootStrapUser(UserInvitation(email, role), pw)

  final def bootStrapUser(inv: UserInvitation, pw: PlainTextPassword): F[User] =
    for {
      token <- uba.bootstrapUser(inv)
      user  <- uca.invitationStep2(token, pw)
      _     <- logger.info(
        s"BOOTSTRAP — inserting user: role=${inv.role.productPrefix} email=${inv.email.plainTextEmail} pw=${pw.plainText}"
      )
    } yield user
}

object ServerBootstrapAlgebra {

  def async[F[_]: Async](uca: UserAccountAlgebra[F], uba: UserAccountBootstrapAlgebra[F]): ServerBootstrapAlgebra[F] =
    new ServerBootstrapAlgebra(uca, uba) {}

}
