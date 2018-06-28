package pms.algebra.user.impl

import cats.syntax.all._
import doobie._
import doobie.implicits._
import pms.algebra.user._
import pms.core._
import pms.effects._
import tsec.jws.mac._
import tsec.jwt._
import tsec.mac.jca._

import scala.concurrent.duration._

/**
  *
  * @author Lorand Szakacs, https://github.com/lorandszakacs
  * @since 21 Jun 2018
  *
  */
final private[user] class AsyncAlgebraImpl[F[_]](
  implicit
  val F:                   Async[F],
  override val monadError: MonadError[F, Throwable],
  val transactor:          Transactor[F]
) extends UserAuthAlgebra()(monadError) with UserAccountAlgebra[F] with UserAlgebra[F] {

  import UserSql._

  override protected def authAlgebra: UserAuthAlgebra[F] = this

  override def authenticate(email: Email, pw: PlainTextPassword): F[AuthCtx] =
    storeAuth(find(email, pw))

  override def authenticate(token: AuthenticationToken): F[AuthCtx] =
    storeAuth(findUserByToken(token))

  override protected[user] def promoteUserOP(id: UserID, newRole: UserRole): F[Unit] =
    F.raiseError(new NotImplementedError("Cannot promote user yet"))

  override protected def registrationStep1OP(
    reg: UserRegistration
  ): F[UserRegistrationToken] =
    F.raiseError(new NotImplementedError("Cannot perform registration step 1 OP at this time"))

  override def registrationStep2(token: UserRegistrationToken): F[User] =
    F.raiseError(new NotImplementedError("Cannot perform registration step 2 at this time"))

  override def resetPasswordStep1(email: Email): F[PasswordResetToken] =
    F.raiseError(new NotImplementedError("Cannot perform resetPassword step 1 at this time"))

  override def resetPasswordStep2(token: PasswordResetToken, newPassword: PlainTextPassword): F[Unit] =
    F.raiseError(new NotImplementedError("Cannot perform resetPassword step 2 at this time"))

  override def findUser(id: UserID)(implicit auth: AuthCtx): F[Option[User]] =
    find(id).transact(transactor)

  private def storeAuth(findUser: => ConnectionIO[Option[User]]): F[AuthCtx] =
    for {
      token <- generateToken()
      user  <- insertToken(findUser, token).transact(transactor)
    } yield AuthCtx(token, user.get)

  private def insertToken(findUser: => ConnectionIO[Option[User]], token: AuthenticationToken) =
    for {
      user <- findUser
      _ <- user match {
            case Some(value) => insertAuthenticationToken(value.id, token)
            case None        => throw new Exception("Unauthorized")
          }
    } yield user

  private def generateToken() =
    for {
      key    <- HMACSHA256.generateKey[F]
      claims <- JWTClaims.withDuration[F](expiration = Some(10.minutes))
      token  <- JWTMac.buildToString[F, HMACSHA256](claims, key)
    } yield AuthenticationToken.haunt(token)
}

object UserSql {
  implicit val userIDMeta: Meta[UserID] = Meta[Long].xmap(
    UserID.haunt,
    UserID.exorcise
  )
  implicit val authenticationTokenMeta: Meta[AuthenticationToken] = Meta[String].xmap(
    AuthenticationToken.haunt,
    AuthenticationToken.exorcise
  )
  implicit val emailMeta:    Meta[Email]             = Meta[String].xmap(Email.apply(_).unsafeGet(),             _.plainTextEmail)
  implicit val pwdMeta:      Meta[PlainTextPassword] = Meta[String].xmap(PlainTextPassword.apply(_).unsafeGet(), _.plainText)
  implicit val userRoleMeta: Meta[UserRole]          = Meta[String].xmap(UserRole.fromName(_).unsafeGet(),       _.toString)
  implicit val userComposite: Composite[User] =
    Composite[(UserID, Email, UserRole)]
      .imap((t: (UserID, Email, UserRole)) => User(t._1, t._2, t._3))((u: User) => (u.id, u.email, u.role))

  def find(id: UserID): ConnectionIO[Option[User]] =
    sql"""SELECT id, email, role FROM users WHERE id=$id""".query[User].option

  def find(email: Email, pwd: PlainTextPassword): ConnectionIO[Option[User]] =
    sql"""SELECT id, email, role FROM users WHERE email=$email AND password=$pwd""".query[User].option

  def find(token: AuthenticationToken): ConnectionIO[Option[Long]] =
    sql"""SELECT userId FROM authentications WHERE token=$token""".query[Long].option

  def insertAuthenticationToken(id: UserID, token: AuthenticationToken): ConnectionIO[Long] =
    sql"""INSERT INTO authentications(userId, token) VALUES($id, $token)""".update.withUniqueGeneratedKeys[Long]("id")

  def findUserByToken(token: AuthenticationToken): ConnectionIO[Option[User]] =
    for {
      userId <- find(token)
      user <- userId match {
               case Some(value) => find(UserID.haunt(value))
               case None        => throw new Exception("Unauthorized")
             }
    } yield user
}
