/*
 * Copyright 2021 BusyMachines
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package phms.algebra.user.impl

import phms.*
import phms.time.*
import phms.db.*
import phms.kernel.*
import phms.algebra.user.*
import phms.logger.*

/** @author Lorand Szakacs, https://github.com/lorandszakacs
  * @since 21 Jun 2018
  */
final private[user] class UserAlgebraImpl[F[_]](using
  F:            MonadCancelThrow[F],
  random:       Random[F],
  secureRandom: SecureRandom[F],
  time:         Time[F],
  dbPool:       DBPool[F],
  logging:      Logging[F],
) extends UserAuthAlgebra[F]()(using F) with UserAccountAlgebra[F] with UserAlgebra[F] {

  private val logger: Logger[F] = logging.named("user_algebra")

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
      _             <- logger.info("attempting email + pw authentication")
      userRepr      <- dbPool.use(session => PSQLUsers(session).findByEmail(email).flatMap(_.liftTo[F](invalidEmailOrPW)))
      _             <- logger.info("user exists")
      validPassword <- UserCrypto.checkUserPassword[F](pw, userRepr.bcryptPW)
      _             <- logger.info(s"is password valid=$validPassword")
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
            _ <- logger.info(s"inserting auth token")
            _ <- dbPool.use(session => PSQLUserAuth(session).insert(ctxRepr))
            _ <- logger.info(s"login successful")
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
            _        <- logger.debug("attempting token authentication")
            ctxRepr  <- user_auths
              .findForToken(token)
              .flatMap(_.liftTo[F](Fail.unauthorized("Invalid authentication token")))
            userRepr <- PSQLUsers(session).findByID(ctxRepr.userID).flatMap(_.liftTo[F](Fail.iscata("", "")))
            _        <- UserAuthExpiration
              .isInPast[F](ctxRepr.expiresAt)
              .ifM(
                ifTrue  = user_auths.deleteToken(token) *> Fail.unauthorized("Auth token expired").raiseError[F, Unit],
                ifFalse = F.unit,
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
