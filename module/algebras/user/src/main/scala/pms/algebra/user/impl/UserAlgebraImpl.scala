package pms.algebra.user.impl

import pms.db._
import pms.algebra.user._
import pms._
import pms.kernel._

/** @author Lorand Szakacs, https://github.com/lorandszakacs
  * @since 21 Jun 2018
  */
final private[user] class UserAlgebraImpl[F[_]](implicit
                                                val F:          MonadThrow[F],
                                                val sr:         SecureRandom[F],
                                                val transactor: SessionPool[F],
) extends UserAuthAlgebra[F]()(F) with UserAccountAlgebra[F] with UserAlgebra[F] {

//  import UserAlgebraSQL._

  override protected def monadThrow:  MonadThrow[F]      = F
  override protected def authAlgebra: UserAuthAlgebra[F] = this

  private val invalidEmailOrPW: Throwable = Fail.unauthorized("Invalid email or password")

  override def authenticate(email: Email, pw: PlainTextPassword): F[AuthCtx] =
    for {
//      userRepr <- findRepr(email).transact(transactor).flatMap {
//        case None    => F.raiseError[UserRepr](invalidEmailOrPW)
//        case Some(v) => F.pure[UserRepr](v)
//      }
      bcryptHash    <- Fail.nicata("User authentication find user by email").raiseError[F, UserCrypto.BcryptPW]
      validPassword <- UserCrypto.checkUserPassword[F](pw, bcryptHash)
      auth          <-
        if (validPassword) {
          for {
            token <- UserCrypto.generateToken[F, AuthenticationToken]
            ctx   <- Fail.nicata("Store user authentication").raiseError[F, AuthCtx]
          } yield ctx
        }
        else invalidEmailOrPW.raiseError[F, AuthCtx]

    } yield auth

  override def authenticate(token: AuthenticationToken): F[AuthCtx] =
    Fail.nicata(s"Authenticate via token: $token").raiseError[F, AuthCtx]

  override protected[user] def promoteUserOP(id: UserID, newRole: UserRole): F[Unit] =
    Fail.nicata(s"promoteUserOP for $id, $newRole").raiseError[F, Unit]

  override protected[user] def registrationStep1Impl(
    inv: UserInvitation
  ): F[UserInviteToken] =
    for {
      token <- UserCrypto.generateToken[F, UserInviteToken]
      _     <- Fail.nicata("Store registration step 1").raiseError[F, Unit]
//      toInsert = UserInvitationSQL.UserInvitationRepr(
//        email           = inv.email,
//        role            = inv.role,
//        invitationToken = token,
//      )
//      _ <- UserInvitationSQL.insert(toInsert).transact(transactor)
    } yield token

  override def invitationStep2(token: UserInviteToken, pw: PlainTextPassword): F[User] =
    for {
      bcrypt <- UserCrypto.hashPWWithBcrypt[F](pw)
      user   <- Fail.nicata(s"User invitation step2. We did bcrypt: $bcrypt").raiseError[F, User]
    } yield user

  override def resetPasswordStep1(email: Email): F[PasswordResetToken] =
    for {
      token <- UserCrypto.generateToken[F, PasswordResetToken]
      _     <- Fail.nicata(s"Reset password step 1. Generated the token: $token").raiseError[F, PasswordResetToken]
    } yield token

  override def resetPasswordStep2(token: PasswordResetToken, newPassword: PlainTextPassword): F[Unit] =
    for {
      hash <- UserCrypto.hashPWWithBcrypt[F](newPassword)
      _    <- Fail.nicata(s"Reset password step 2: new hash: $hash").raiseError[F, Unit]
    } yield ()

  override def findUser(id: UserID)(implicit auth: AuthCtx): F[Option[User]] =
    Fail.nicata(s"find user by id $id").raiseError[F, Option[User]]

}
