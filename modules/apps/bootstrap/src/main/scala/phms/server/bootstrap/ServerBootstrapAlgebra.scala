package phms.server.bootstrap

import phms._
import phms.kernel._
import phms.logger._
import phms.algebra.user._

/** This should be used only in development, and testing!
  *
  * @author Lorand Szakacs, https://github.com/lorandszakacs
  * @since 13 Jul 2018
  */
final private[bootstrap] class ServerBootstrapAlgebra[F[_]] private (
  private val uca: UserAccountAlgebra[F],
  private val uba: UserAccountBootstrapAlgebra[F],
)(implicit F:      MonadThrow[F], logging: Logging[F]) {

  private val logger = logging.of(this)

  def bootStrapSuperAdmin(email: Email, pw: PlainTextPassword): F[User] =
    bootStrapUser(email, pw, UserRole.SuperAdmin)

  def bootStrapUser(email: Email, pw: PlainTextPassword, role: UserRole): F[User] =
    this.bootStrapUser(UserInvitation(email, role), pw)

  def bootStrapUser(inv: UserInvitation, pw: PlainTextPassword): F[User] =
    for {
      _     <- logger.info(
        s"BOOTSTRAP — inserting user invite: role=${inv.role.productPrefix} email=${inv.email} pw=$pw."
      )
      token <- uba.bootstrapUser(inv)
      _     <- logger.info(s"BOOTSTRAP — done inserting user invite")
      user  <- uca.invitationStep2(token, pw)
      _     <- logger.info(
        s"BOOTSTRAP — inserted user: role=${inv.role.productPrefix} email=${inv.email} pw=$pw"
      )
    } yield user
}

private[bootstrap] object ServerBootstrapAlgebra {

  def create[F[_]](
    uca:        UserAccountAlgebra[F],
    uba:        UserAccountBootstrapAlgebra[F],
  )(implicit F: MonadThrow[F], logging: Logging[F]): ServerBootstrapAlgebra[F] = new ServerBootstrapAlgebra(uca, uba)

}
