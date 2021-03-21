//package pms.algebra.user.impl
//
//import doobie._
//import doobie.implicits._
//import pms.algebra.user._
//import pms.core._
//
///**
//  *
//  * @author Lorand Szakacs, https://github.com/lorandszakacs
//  * @since 22 Mar 2019
//  *
//  */
//private[impl] object UserAlgebraSQL {
//
//  import UserAlgebraMetas._
//
//  private[impl] case class UserRepr(
//    email: Email,
//    pw:    UserCrypto.BcryptPW,
//    role:  UserRole,
//  )
//
//  implicit val userReprComposite: Read[UserRepr] =
//    Read[(Email, String, UserRole)]
//      .imap((t: (Email, String, UserRole)) => UserRepr(t._1, UserCrypto.BcryptPW(t._2), t._3))((u: UserRepr) =>
//        (u.email, u.pw.toString, u.role)
//      )
//
//  def updateRole(id: UserID, role: UserRole): ConnectionIO[Int] =
//    sql"""UPDATE users SET role=$role WHERE id=$id""".update.run
//
//  def updatePasswordToken(id: UserID, token: PasswordResetToken): ConnectionIO[Int] =
//    sql"""UPDATE users SET passwordReset=$token WHERE id=$id""".update.run
//
//  def updatePassword(id: UserID, newPassword: UserCrypto.BcryptPW): ConnectionIO[Int] =
//    sql"""UPDATE users SET password=${newPassword.toString} WHERE id=$id""".update.run
//
//  def find(id: UserID): ConnectionIO[Option[User]] =
//    sql"""SELECT id, email, role FROM users WHERE id=$id""".query[User].option
//
//  def find(email: Email, pwd: UserCrypto.BcryptPW): ConnectionIO[Option[User]] =
//    sql"""SELECT id, email, role FROM users WHERE email=$email AND password=${pwd.toString}"""
//      .query[User]
//      .option
//
//  def find(email: Email): ConnectionIO[Option[User]] =
//    sql"""SELECT id, email, role FROM users WHERE email=$email"""
//      .query[User]
//      .option
//
//  def findRepr(email: Email): ConnectionIO[Option[UserRepr]] =
//    sql"""SELECT email, password, role FROM users WHERE email=$email"""
//      .query[UserRepr]
//      .option
//
//  def findByAuthToken(token: AuthenticationToken): ConnectionIO[Option[Long]] =
//    sql"""SELECT userId FROM authentications WHERE token=$token"""
//      .query[Long]
//      .option
//
//  def findByPwdToken(token: PasswordResetToken): ConnectionIO[Option[User]] =
//    sql"""SELECT id, email, role FROM users WHERE passwordReset=$token"""
//      .query[User]
//      .option
//
//  def insert(repr: UserRepr): ConnectionIO[UserID] =
//    sql"""INSERT INTO users(email, password, role) VALUES (${repr.email}, ${repr.pw.toString}, ${repr.role})""".update
//      .withUniqueGeneratedKeys[Long]("id")
//      .map(UserID.spook)
//
//  def insertAuthenticationToken(id: UserID, token: AuthenticationToken): ConnectionIO[Long] =
//    sql"""INSERT INTO authentications(userId, token) VALUES($id, $token)""".update
//      .withUniqueGeneratedKeys[Long]("id")
//
//  def findUserByAuthToken(token: AuthenticationToken): ConnectionIO[Option[User]] =
//    for {
//      userId <- findByAuthToken(token)
//      user   <- userId match {
//        case Some(value) => find(UserID(value))
//        case None        =>
//          throw new Exception("Unauthorized") //FIXME: replace with proper error, DO NOT THROW!
//      }
//    } yield user
//
//  def insertToken(findUser: => ConnectionIO[Option[User]], token: AuthenticationToken): ConnectionIO[Option[User]] =
//    for {
//      user <- findUser
//      _    <- user match {
//        case Some(value) => insertAuthenticationToken(value.id, token)
//        case None        =>
//          throw new Exception("Unauthorized") //FIXME: replace with proper error, DO NOT THROW!
//      }
//    } yield user
//
//  def updatePwdToken(email: Email, token: PasswordResetToken): ConnectionIO[Option[User]] =
//    for {
//      user <- find(email)
//      _    <- updatePasswordToken(user.get.id, token)
//    } yield user
//
//  def changePassword(token: PasswordResetToken, newPassword: UserCrypto.BcryptPW): ConnectionIO[Unit] =
//    for {
//      user <- findByPwdToken(token)
//      _    <- user match {
//        case Some(value) => updatePassword(value.id, newPassword)
//        case None        => throw new Exception("User not found")
//      }
//    } yield ()
//}
