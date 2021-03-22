package phms.algebra.user.impl

import phms._
import phms.time._
import phms.db._
import phms.kernel._
import phms.algebra.user._

/** @author Lorand Szakacs, https://github.com/lorandszakacs
  * @since 21 Jun 2018
  */
final private[user] class UserAlgebraImpl[F[_]](implicit
  val F:      MonadCancelThrow[F],
  val sr:     SecureRandom[F],
  val time:   Time[F],
  val dbPool: DDPool[F],
) extends UserAuthAlgebra[F]()(F) with UserAccountAlgebra[F] with UserAlgebra[F] {

  override protected def monadThrow:  MonadThrow[F]      = F
  override protected def authAlgebra: UserAuthAlgebra[F] = this

  private val invalidEmailOrPW: Throwable = Fail.unauthorized("Invalid email or password")

  override def authenticate(email: Email, pw: PlainTextPassword): F[AuthCtx] =
    for {
//      userRepr <- findRepr(email).transact(dbPool).flatMap {
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
      token     <- UserCrypto.generateToken[F, UserInviteToken]
      //TODO: make expiry time configurable, and inject it here
      expiresAt <- UserInviteExpiration.tomorrow[F]
      toInsert = PSQLUserInvitations.UserInvitationRepr(
        email           = inv.email,
        role            = inv.role,
        invitationToken = token,
        expiresAt       = expiresAt,
      )
      _ <- dbPool.use { session =>
        val users        = PSQLUsers[F](session)
        val user_invites = PSQLUserInvitations[F](session)
        session.transaction.use { _ =>
          for {
            optUser   <- users.findByEmail(inv.email)
            _         <-
              //TODO: add syntax for failing on any F[Option[T]] on some, so we don't repeat the else branch
              if (optUser.isDefined)
                Fail.conflict(s"User w/ email: ${inv.email} already exists").raiseError[F, Unit]
              else F.unit
            //TODO: idem
            optInvite <- user_invites.findByEmail(inv.email)
            _         <-
              if (optInvite.isDefined)
                Fail
                  .conflict(
                    s"""|User invite for email: ${inv.email} already exists. 
                        |We currently do not support refreshing invites, 
                        |maybe thing of this as a new feature?
                        """.stripMargin
                  )
                  .raiseError[F, Unit]
              else F.unit
            _         <- user_invites.insert(toInsert)
          } yield ()
        }
      }
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
