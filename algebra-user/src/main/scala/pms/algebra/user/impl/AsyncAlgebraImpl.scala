package pms.algebra.user.impl

import busymachines.core.UnauthorizedFailure
import cats.syntax.all._
import doobie._
import doobie.implicits._
import pms.algebra.user._
import pms.core._
import pms.effects._
import tsec.jws.mac._
import tsec.jwt._
import tsec.mac.jca._
import tsec.passwordhashers._
import tsec.passwordhashers.jca._

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

  private val invalidEmailOrPW = UnauthorizedFailure("Invalid email or password")

  override def authenticate(email: Email, pw: PlainTextPassword): F[AuthCtx] =
    for {
      userRepr <- findRepr(email).transact(transactor).flatMap {
                   case None    => F.raiseError[UserRepr](invalidEmailOrPW)
                   case Some(v) => F.pure[UserRepr](v)
                 }
      validPW <- BCrypt.checkpw[F](pw.plainText, userRepr.pw)
      auth <- validPW match {
               case tsec.common.Verified           => storeAuth(find(email))
               case tsec.common.VerificationFailed => F.raiseError[AuthCtx](invalidEmailOrPW)
             }
    } yield auth

  override def authenticate(token: AuthenticationToken): F[AuthCtx] =
    storeAuth(findUserByAuthToken(token))

  override protected[user] def promoteUserOP(id: UserID, newRole: UserRole): F[Unit] =
    updateRole(id, newRole).transact(transactor).map(_ => ())

  override protected[user] def registrationStep1OP(
    reg: UserRegistration
  ): F[UserRegistrationToken] =
    for {
      token      <- generateToken()
      scryptHash <- hashPWWithScrypt(reg.pw)
      repr = UserRepr(email = reg.email, pw = scryptHash, role = reg.role)
      _ <- insert(repr, UserRegistrationToken(token)).transact(transactor)
    } yield UserRegistrationToken(token)

  override def registrationStep2(token: UserRegistrationToken): F[User] =
    updateRegToken(token)
      .transact(transactor)
      .map {
        case Some(value) => value
        case None        => throw new Exception("User not found")
      }

  override def resetPasswordStep1(email: Email): F[PasswordResetToken] =
    for {
      token <- generateToken()
      _     <- updatePwdToken(email, PasswordResetToken(token)).transact(transactor)
    } yield PasswordResetToken(token)

  override def resetPasswordStep2(token: PasswordResetToken, newPassword: PlainTextPassword): F[Unit] =
    for {
      hash <- hashPWWithScrypt(newPassword)
      _    <- changePassword(token, hash).transact(transactor)
    } yield ()

  override def findUser(id: UserID)(implicit auth: AuthCtx): F[Option[User]] =
    find(id).transact(transactor)

  private def storeAuth(findUser: => ConnectionIO[Option[User]]): F[AuthCtx] =
    for {
      token <- generateToken()
      user  <- insertToken(findUser, AuthenticationToken(token)).transact(transactor)
    } yield AuthCtx(AuthenticationToken(token), user.get)

  private def generateToken(): F[String] =
    for {
      key    <- HMACSHA256.generateKey[F]
      claims <- JWTClaims.withDuration[F](expiration = Some(10.minutes))
      token  <- JWTMac.buildToString[F, HMACSHA256](claims, key)
    } yield token

  private def hashPWWithScrypt(ptpw: PlainTextPassword): F[BcryptPW] =
    BCrypt.hashpw[F](ptpw.plainText)

}

private[impl] object UserSql {
  type BcryptPW = PasswordHash[BCrypt]
  def BcryptPW(pt: String): BcryptPW = PasswordHash[BCrypt](pt)

  private[impl] case class UserRepr(
    email: Email,
    pw:    BcryptPW,
    role:  UserRole
  )

  /*_*/
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
  implicit val passwordResetTokenMeta: Meta[PasswordResetToken] = Meta[String].xmap(
    PasswordResetToken.haunt,
    PasswordResetToken.exorcise
  )
  implicit val emailMeta:    Meta[Email]             = Meta[String].xmap(Email.apply(_).unsafeGet(),             _.plainTextEmail)
  implicit val pwdMeta:      Meta[PlainTextPassword] = Meta[String].xmap(PlainTextPassword.apply(_).unsafeGet(), _.plainText)
  implicit val userRoleMeta: Meta[UserRole]          = Meta[String].xmap(UserRole.fromName(_).unsafeGet(),       _.toString)

  implicit val userComposite: Composite[User] =
    Composite[(UserID, Email, UserRole)]
      .imap((t: (UserID, Email, UserRole)) => User(t._1, t._2, t._3))((u: User) => (u.id, u.email, u.role))

  implicit val userReprComposite: Composite[UserRepr] =
    Composite[(Email, String, UserRole)]
      .imap((t: (Email, String, UserRole)) => UserRepr(t._1, BcryptPW(t._2), t._3))(
        (u: UserRepr) => (u.email, u.pw.toString, u.role)
      )
  /*_*/

  def updateRole(id: UserID, role: UserRole): ConnectionIO[Int] =
    sql"""UPDATE users SET role=$role WHERE id=$id""".update.run

  def updateRegistrationToken(id: UserID, token: UserRegistrationToken): ConnectionIO[Int] =
    sql"""UPDATE users SET registration=$token WHERE id=$id""".update.run

  def updatePasswordToken(id: UserID, token: PasswordResetToken): ConnectionIO[Int] =
    sql"""UPDATE users SET passwordReset=$token WHERE id=$id""".update.run

  def updatePassword(id: UserID, newPassword: BcryptPW): ConnectionIO[Int] =
    sql"""UPDATE users SET password=${newPassword.toString} WHERE id=$id""".update.run

  def find(id: UserID): ConnectionIO[Option[User]] =
    sql"""SELECT id, email, role FROM users WHERE id=$id""".query[User].option

  def find(email: Email, pwd: BcryptPW): ConnectionIO[Option[User]] =
    sql"""SELECT id, email, role FROM users WHERE email=$email AND password=${pwd.toString}""".query[User].option

  def find(email: Email): ConnectionIO[Option[User]] =
    sql"""SELECT id, email, role FROM users WHERE email=$email""".query[User].option

  def findRepr(email: Email): ConnectionIO[Option[UserRepr]] =
    sql"""SELECT email, password, role FROM users WHERE email=$email""".query[UserRepr].option

  def findByAuthToken(token: AuthenticationToken): ConnectionIO[Option[Long]] =
    sql"""SELECT userId FROM authentications WHERE token=$token""".query[Long].option

  def findByRegToken(token: UserRegistrationToken): ConnectionIO[Option[User]] =
    sql"""SELECT id, email, role FROM users WHERE registration=$token""".query[User].option

  def findByPwdToken(token: PasswordResetToken): ConnectionIO[Option[User]] =
    sql"""SELECT id, email, role FROM users WHERE passwordReset=$token""".query[User].option

  def insert(repr: UserRepr, token: UserRegistrationToken): ConnectionIO[Long] = {
    sql"""INSERT INTO users(email, password, role, registration) VALUES (${repr.email}, ${repr.pw.toString}, ${repr.role}, $token)""".update
      .withUniqueGeneratedKeys[Long]("id")
  }

  def insertAuthenticationToken(id: UserID, token: AuthenticationToken): ConnectionIO[Long] =
    sql"""INSERT INTO authentications(userId, token) VALUES($id, $token)""".update.withUniqueGeneratedKeys[Long]("id")

  def findUserByAuthToken(token: AuthenticationToken): ConnectionIO[Option[User]] =
    for {
      userId <- findByAuthToken(token)
      user <- userId match {
               case Some(value) => find(UserID(value))
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

  def updatePwdToken(email: Email, token: PasswordResetToken): ConnectionIO[Option[User]] =
    for {
      user <- find(email)
      _    <- updatePasswordToken(user.get.id, token)
    } yield user

  def changePassword(token: PasswordResetToken, newPassword: BcryptPW): ConnectionIO[Unit] =
    for {
      user <- findByPwdToken(token)
      _ <- user match {
            case Some(value) => updatePassword(value.id, newPassword)
            case None        => throw new Exception("User not found")
          }
    } yield ()
}
