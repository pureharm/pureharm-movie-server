package pms.algebra.user.impl

import pms.db._
import pms.algebra.user._
import pms._

/**
  *
  * @author Lorand Szakacs, https://github.com/lorandszakacs
  * @since 21 Jun 2018
  *
  */
final private[user] class UserAlgebraImpl[F[_]](implicit
  val F:          Async[F],
  val transactor: Transactor[F],
) extends UserAuthAlgebra[F]()(F) with UserAccountAlgebra[F] with UserAlgebra[F] {
//
////  import UserAlgebraSQL._
//
//  override protected def monadError:  MonadError[F, Throwable] = F
//  override protected def authAlgebra: UserAuthAlgebra[F]       = this
//
//  private val invalidEmailOrPW: Throwable = Fail.unauthorized("Invalid email or password")
//
//  override def authenticate(email: Email, pw: PlainTextPassword): F[AuthCtx] =
//    for {
//      userRepr <- findRepr(email).transact(transactor).flatMap {
//        case None    => F.raiseError[UserRepr](invalidEmailOrPW)
//        case Some(v) => F.pure[UserRepr](v)
//      }
//      auth     <-
//        UserCrypto
//          .checkUserPassword[F](pw.plainText, userRepr.pw)
//          .flatMap {
//            case true  => storeAuth(find(email))
//            case false => F.raiseError[AuthCtx](invalidEmailOrPW)
//          }
//
//    } yield auth
//
//  override def authenticate(token: AuthenticationToken): F[AuthCtx] =
//    storeAuth(findUserByAuthToken(token))
//
//  override protected[user] def promoteUserOP(id: UserID, newRole: UserRole): F[Unit] =
//    updateRole(id, newRole).transact(transactor).map(_ => ())
//
//  override protected[user] def registrationStep1Impl(
//    inv: UserInvitation
//  ): F[UserInviteToken] =
//    for {
//      token <- UserCrypto.generateToken(F).map(UserRegistrationToken.spook)
//      toInsert = UserInvitationSQL.UserInvitationRepr(
//        email           = inv.email,
//        role            = inv.role,
//        invitationToken = token,
//      )
//      _ <- UserInvitationSQL.insert(toInsert).transact(transactor)
//    } yield token
//
//  override def invitationStep2(token: UserInviteToken, pw: PlainTextPassword): F[User] = {
//    val cio: ConnectionIO[User] = for {
//      invite <- UserInvitationSQL.findByToken(token).flatMap { opt =>
//        opt.liftTo[ConnectionIO](new RuntimeException("No user invitation found"))
//      }
//      hashed <- UserCrypto.hashPWWithBcrypt[ConnectionIO](pw)
//      userRepr = UserRepr(
//        email = invite.email,
//        pw    = hashed,
//        role  = invite.role,
//      )
//      userId <- UserAlgebraSQL.insert(userRepr)
//      _                <- UserInvitationSQL.deleteByToken(token)
//      newlyCreatedUser <- UserAlgebraSQL.find(userId).flatMap { opt =>
//        opt.liftTo[ConnectionIO](new Error("No user found even after we created them. WTF? This is a bug"))
//      }
//    } yield newlyCreatedUser
//
//    cio.transact(transactor)
//  }
//
//  override def resetPasswordStep1(email: Email): F[PasswordResetToken] =
//    for {
//      token <- UserCrypto.generateToken(F)
//      _     <- updatePwdToken(email, PasswordResetToken(token)).transact(transactor)
//    } yield PasswordResetToken(token)
//
//  override def resetPasswordStep2(token: PasswordResetToken, newPassword: PlainTextPassword): F[Unit] =
//    for {
//      hash <- UserCrypto.hashPWWithBcrypt(newPassword)(F)
//      _    <- changePassword(token, hash).transact(transactor)
//    } yield ()
//
//  override def findUser(id: UserID)(implicit auth: AuthCtx): F[Option[User]] =
//    find(id).transact(transactor)
//
//  private def storeAuth(findUser: => ConnectionIO[Option[User]]): F[AuthCtx] =
//    for {
//      token <- UserCrypto.generateToken(F)
//      user  <- insertToken(findUser, AuthenticationToken(token))
//        .transact(transactor)
//    } yield AuthCtx(AuthenticationToken(token), user.get)

  override def authenticate(email: Email, pw: PlainTextPassword): F[AuthCtx] = ???

  override def authenticate(token: AuthenticationToken): F[AuthCtx] = ???

  override protected[user] def promoteUserOP(id: UserID, newRole: UserRole): F[Unit] = ???

  implicit override protected def monadError: MonadError[F, Throwable] = ???

  override protected def authAlgebra: UserAuthAlgebra[F] = ???

  override protected[user] def registrationStep1Impl(inv: UserInvitation): F[UserInviteToken] = ???

  override def invitationStep2(token: UserInviteToken, pw: PlainTextPassword): F[User] = ???

  override def resetPasswordStep1(email: Email): F[PasswordResetToken] = ???

  override def resetPasswordStep2(token: PasswordResetToken, newPassword: PlainTextPassword): F[Unit] = ???

  override def findUser(id: UserID)(implicit auth: AuthCtx): F[Option[User]] = ???
}
