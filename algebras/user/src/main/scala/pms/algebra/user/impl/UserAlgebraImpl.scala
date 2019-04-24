package pms.algebra.user.impl

import busymachines.core.UnauthorizedFailure

import doobie._
import doobie.implicits._
import cats.implicits._

import pms.algebra.user._
import pms.core._
import pms.effects._

/**
  *
  * @author Lorand Szakacs, https://github.com/lorandszakacs
  * @since 21 Jun 2018
  *
  */
final private[user] class UserAlgebraImpl[F[_]] private (
  implicit
  val F:          Async[F],
  val transactor: Transactor[F],
) extends UserAuthAlgebra[F]()(F) with UserAccountAlgebra[F] with UserAlgebra[F] {

  import UserAlgebraSQL._

  override protected def monadError:  MonadError[F, Throwable] = F
  override protected def authAlgebra: UserAuthAlgebra[F]       = this

  private val invalidEmailOrPW: Throwable = UnauthorizedFailure("Invalid email or password")

  override def authenticate(email: Email, pw: PlainTextPassword): F[AuthCtx] =
    for {
      userRepr <- findRepr(email).transact(transactor).flatMap {
        case None    => F.raiseError[UserRepr](invalidEmailOrPW)
        case Some(v) => F.pure[UserRepr](v)
      }
      auth <- UserCrypto.checkUserPassword[F](pw.plainText, userRepr.pw).flatMap {
        case true  => storeAuth(find(email))
        case false => F.raiseError[AuthCtx](invalidEmailOrPW)
      }

    } yield auth

  override def authenticate(token: AuthenticationToken): F[AuthCtx] =
    storeAuth(findUserByAuthToken(token))

  override protected[user] def promoteUserOP(id: UserID, newRole: UserRole): F[Unit] =
    updateRole(id, newRole).transact(transactor).map(_ => ())

  override protected[user] def registrationStep1Impl(
    inv: UserInvitation,
  ): F[UserRegistrationToken] =
    for {
      token <- UserCrypto.generateToken(F).map(UserRegistrationToken.spook)
      toInsert = UserInvitationSQL.UserInvitationRepr(
        email           = inv.email,
        role            = inv.role,
        invitationToken = token,
      )
      _ <- UserInvitationSQL.insert(toInsert).transact(transactor)
    } yield token

  override def registrationStep2(token: UserRegistrationToken, pw: PlainTextPassword): F[User] = {
    val cio: ConnectionIO[User] = for {
      invite <- UserInvitationSQL.findByToken(token).flatMap { opt =>
        opt.liftTo[ConnectionIO](new RuntimeException("No user invitation found"))
      }
      hashed <- UserCrypto.hashPWWithBcrypt[ConnectionIO](pw)
      userRepr = UserRepr(
        email = invite.email,
        pw    = hashed,
        role  = invite.role,
      )
      userId <- UserAlgebraSQL.insert(userRepr)
      _      <- UserInvitationSQL.deleteByToken(token)
      newlyCreatedUser <- UserAlgebraSQL.find(userId).flatMap { opt =>
        opt.liftTo[ConnectionIO](new Error("No user found even after we created them. WTF? This is a bug"))
      }
    } yield newlyCreatedUser

    cio.transact(transactor)
  }

  override def resetPasswordStep1(email: Email): F[PasswordResetToken] =
    for {
      token <- UserCrypto.generateToken(F)
      _     <- updatePwdToken(email, PasswordResetToken(token)).transact(transactor)
    } yield PasswordResetToken(token)

  override def resetPasswordStep2(token: PasswordResetToken, newPassword: PlainTextPassword): F[Unit] =
    for {
      hash <- UserCrypto.hashPWWithBcrypt(newPassword)(F)
      _    <- changePassword(token, hash).transact(transactor)
    } yield ()

  override def findUser(id: UserID)(implicit auth: AuthCtx): F[Option[User]] =
    find(id).transact(transactor)

  private def storeAuth(findUser: => ConnectionIO[Option[User]]): F[AuthCtx] =
    for {
      token <- UserCrypto.generateToken(F)
      user  <- insertToken(findUser, AuthenticationToken(token)).transact(transactor)
    } yield AuthCtx(AuthenticationToken(token), user.get)
}

private[user] object UserAlgebraImpl {
  def async[F[_]: Async: Transactor]: F[UserAlgebraImpl[F]] = Async.apply[F].pure(new UserAlgebraImpl[F]())
}
