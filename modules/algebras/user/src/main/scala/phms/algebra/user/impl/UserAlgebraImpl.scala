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
  val r:      Random[F],
  val sr:     SecureRandom[F],
  val time:   Time[F],
  val dbPool: DBPool[F],
) extends UserAuthAlgebra[F]()(F) with UserAccountAlgebra[F] with UserAlgebra[F] {

  override protected def monadThrow:  MonadThrow[F]      = F
  override protected def authAlgebra: UserAuthAlgebra[F] = this

  private val invalidEmailOrPW: Throwable = Fail.unauthorized("Invalid email or password")

  /** Question?
    *
    * TODO: ponder: should we check the user record in the bounds of the same transaction or keep two
    * separate dbPool calls?
    */
  override def authenticate(email: Email, pw: PlainTextPassword): F[AuthCtx] =
    for {
      userRepr      <- dbPool.use(session => PSQLUsers(session).findByEmail(email).flatMap(_.liftTo[F](invalidEmailOrPW)))
      validPassword <- UserCrypto.checkUserPassword[F](pw, userRepr.bcryptPW)
      tuple         <-
        if (validPassword) {
          for {
            token     <- UserCrypto.generateToken[F, AuthenticationToken]
            //TODO: make token expiration configurable
            expiresAt <- UserAuthExpiration.tomorrow[F]
            ctxRepr = PSQLUserAuth.UserAuthRepr(
              token     = token,
              userID    = userRepr.id,
              expiresAt = expiresAt,
            )
            _ <- dbPool.use(session => PSQLUserAuth(session).insert(ctxRepr))
          } yield (userRepr, ctxRepr)
        }
        else invalidEmailOrPW.raiseError[F, (PSQLUsers.UserRepr, PSQLUserAuth.UserAuthRepr)]

    } yield fromRepr(tuple)

  override def authenticate(token: AuthenticationToken): F[AuthCtx] =
    dbPool
      .use { session =>
        session.transaction.use { _ =>
          val user_auths = PSQLUserAuth(session)
          for {
            ctxRepr  <- user_auths
              .findForToken(token)
              .flatMap(_.liftTo[F](Fail.unauthorized("Invalid authentication token")))
            userRepr <- PSQLUsers(session).findByID(ctxRepr.userID).flatMap(_.liftTo[F](Fail.iscata("", "")))
            _        <- UserAuthExpiration
              .isInPast[F](ctxRepr.expiresAt)
              .ifM(
                ifTrue  = Fail.unauthorized("Auth token expired").raiseError[F, Unit],
                ifFalse = user_auths.deleteToken(token),
              )
          } yield (userRepr, ctxRepr)
        }
      }
      .map(fromRepr)

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

  override def undoInvitationStep1(token: UserInviteToken): F[Unit] =
    dbPool.use(session => PSQLUserInvitations(session).deleteByInvite(token))

  override def invitationStep2(token: UserInviteToken, pw: PlainTextPassword): F[User] =
    for {
      bcrypt    <- UserCrypto.hashPWWithBcrypt[F](pw)
      newUserID <- UserID.generate[F]
      user      <- dbPool.use { session =>
        val users            = PSQLUsers(session)
        val user_invitations = PSQLUserInvitations(session)
        for {
          invite <- user_invitations
            .findByInvite(token)
            .flatMap(_.liftTo[F](Fail.invalid(s"Invalid user invite token: $token")))

          isExpired <- UserInviteExpiration.isInPast[F](invite.expiresAt)
          _         <-
            if (isExpired) Fail.invalid(s"User invitation expired @ ${invite.expiresAt}").raiseError[F, Unit]
            else F.unit

          newUserRepr = PSQLUsers.UserRepr(
            id           = newUserID,
            email        = invite.email,
            role         = invite.role,
            bcryptPW     = bcrypt,
            pwResetToken = Option.empty,
          )
          _ <- users.insert(newUserRepr)
        } yield fromRepr(newUserRepr)
      }
    } yield user

  override def resetPasswordStep1(email: Email): F[PasswordResetToken] =
    for {
      token <- UserCrypto.generateToken[F, PasswordResetToken]
      _     <- Fail.nicata(s"Reset password step 1. Generated the token: $token").raiseError[F, PasswordResetToken]
    } yield token

  override def undoPasswordResetStep1(email: Email): F[Unit] =
    Fail.nicata(s"Undo password step 1 for email: $email").raiseError[F, Unit]

  override def resetPasswordStep2(token: PasswordResetToken, newPassword: PlainTextPassword): F[Unit] =
    for {
      hash <- UserCrypto.hashPWWithBcrypt[F](newPassword)
      _    <- Fail.nicata(s"Reset password step 2: new hash: $hash").raiseError[F, Unit]
    } yield ()

  override def findUser(id: UserID)(implicit auth: AuthCtx): F[Option[User]] =
    //TODO: implement security policy for user retrieval
    dbPool.use(session => PSQLUsers(session).findByID(id).map(_.map(fromRepr)))

  private def fromRepr(u: PSQLUsers.UserRepr): User = User(
    id    = u.id,
    email = u.email,
    role  = u.role,
  )

  private def fromRepr(u: (PSQLUsers.UserRepr, PSQLUserAuth.UserAuthRepr)): AuthCtx = AuthCtx(
    token = u._2.token,
    user  = fromRepr(u._1),
  )
}
