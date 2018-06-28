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
    storeAuth(findUserByAuthToken(token))

  override protected[user] def promoteUserOP(id: UserID, newRole: UserRole): F[Unit] =
    updateRole(id, newRole).transact(transactor).map(_ => ())

  override protected def registrationStep1OP(
    reg: UserRegistration
  ): F[UserRegistrationToken] =
    for {
      token <- generateToken()
      _     <- insert(reg, UserRegistrationToken.haunt(token)).transact(transactor)
    } yield UserRegistrationToken.haunt(token)

  override def registrationStep2(token: UserRegistrationToken): F[User] =
    updateRegToken(token)
      .transact(transactor)
      .map {
        case Some(value) => value
        case None        => throw new Exception("User not found")
      }

  override def resetPasswordStep1(email: Email): F[PasswordResetToken] =
    F.raiseError(new NotImplementedError("Cannot perform resetPassword step 1 at this time"))

  override def resetPasswordStep2(token: PasswordResetToken, newPassword: PlainTextPassword): F[Unit] =
    F.raiseError(new NotImplementedError("Cannot perform resetPassword step 2 at this time"))

  override def findUser(id: UserID)(implicit auth: AuthCtx): F[Option[User]] =
    find(id).transact(transactor)

  private def storeAuth(findUser: => ConnectionIO[Option[User]]): F[AuthCtx] =
    for {
      token <- generateToken()
      user  <- insertToken(findUser, AuthenticationToken.haunt(token)).transact(transactor)
    } yield AuthCtx(AuthenticationToken.haunt(token), user.get)

  private def generateToken() =
    for {
      key    <- HMACSHA256.generateKey[F]
      claims <- JWTClaims.withDuration[F](expiration = Some(10.minutes))
      token  <- JWTMac.buildToString[F, HMACSHA256](claims, key)
    } yield token
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
  implicit val userRegistrationTokenMeta: Meta[UserRegistrationToken] = Meta[String].xmap(
    UserRegistrationToken.haunt,
    UserRegistrationToken.exorcise
  )
  implicit val emailMeta:    Meta[Email]             = Meta[String].xmap(Email.apply(_).unsafeGet(),             _.plainTextEmail)
  implicit val pwdMeta:      Meta[PlainTextPassword] = Meta[String].xmap(PlainTextPassword.apply(_).unsafeGet(), _.plainText)
  implicit val userRoleMeta: Meta[UserRole]          = Meta[String].xmap(UserRole.fromName(_).unsafeGet(),       _.toString)
  implicit val userComposite: Composite[User] =
    Composite[(UserID, Email, UserRole)]
      .imap((t: (UserID, Email, UserRole)) => User(t._1, t._2, t._3))((u: User) => (u.id, u.email, u.role))

  def updateRole(id: UserID, role: UserRole): ConnectionIO[Int] =
    sql"""UPDATE users SET role=$role WHERE id=$id""".update.run

  def updateRegistrationToken(id: UserID, token: UserRegistrationToken): ConnectionIO[Int] =
    sql"""UPDATE users SET registration=$token WHERE id=$id""".update.run

  def find(id: UserID): ConnectionIO[Option[User]] =
    sql"""SELECT id, email, role FROM users WHERE id=$id""".query[User].option

  def find(email: Email, pwd: PlainTextPassword): ConnectionIO[Option[User]] =
    sql"""SELECT id, email, role FROM users WHERE email=$email AND password=$pwd""".query[User].option

  def findByAuthToken(token: AuthenticationToken): ConnectionIO[Option[Long]] =
    sql"""SELECT userId FROM authentications WHERE token=$token""".query[Long].option

  def findByRegToken(token: UserRegistrationToken): ConnectionIO[Option[User]] =
    sql"""SELECT id, email, role FROM users WHERE registration=$token""".query[User].option

  def insert(reg: UserRegistration, token: UserRegistrationToken): ConnectionIO[Long] =
    sql"""INSERT INTO users(email, password, role, registration) VALUES (${reg.email}, ${reg.pw}, ${reg.role}, $token)""".update
      .withUniqueGeneratedKeys[Long]("id")

  def insertAuthenticationToken(id: UserID, token: AuthenticationToken): ConnectionIO[Long] =
    sql"""INSERT INTO authentications(userId, token) VALUES($id, $token)""".update.withUniqueGeneratedKeys[Long]("id")

  def findUserByAuthToken(token: AuthenticationToken): ConnectionIO[Option[User]] =
    for {
      userId <- findByAuthToken(token)
      user <- userId match {
               case Some(value) => find(UserID.haunt(value))
               case None        => throw new Exception("Unauthorized")
             }
    } yield user

  def updateRegToken(token: UserRegistrationToken): ConnectionIO[Option[User]] =
    for {
      user <- findByRegToken(token)
      _    <- updateRegistrationToken(user.get.id, token)
    } yield user

  def insertToken(findUser: => ConnectionIO[Option[User]], token: AuthenticationToken): ConnectionIO[Option[User]] =
    for {
      user <- findUser
      _ <- user match {
            case Some(value) => insertAuthenticationToken(value.id, token)
            case None        => throw new Exception("Unauthorized")
          }
    } yield user
}
