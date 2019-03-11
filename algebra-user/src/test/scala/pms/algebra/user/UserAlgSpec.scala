package pms.algebra.user

import doobie.util.transactor.Transactor
import pms.core.{Email,               PlainTextPassword}
import pms.db.config.{DatabaseConfig, DatabaseConfigAlgebra}
import pms.effects._

import scala.concurrent.ExecutionContext

class UserAlgSpec extends org.specs2.mutable.Specification with ModuleUserAsync[IO] with ModuleUserBootstrap[IO] {

  val databaseConfig =
    DatabaseConfig("org.postgresql.Driver", "jdbc:postgresql:testmoviedatabase", "busyuser", "qwerty", false)

  implicit val cs = IO.contextShift(ExecutionContext.global)
  implicit def transactor: Transactor[IO] = DatabaseConfigAlgebra.transactor(databaseConfig).unsafeRunSync()
  implicit val userAccount      = UserAccountAlgebra.async(async, transactor)
  implicit def userBootstrapAlg = UserAccountBootstrapAlgebra.impl(userAccount)

  DatabaseConfigAlgebra.initializeSQLDb(databaseConfig).unsafeRunSync()

  val (user: User, email: Email, userRole: UserRole, userPw: PlainTextPassword) =
    TestHelpersFunctions.unsafeCreateUser("SuperAdmin@yahoo.com", "SuperAdmin", "password1234")

  implicit def async: Async[IO] = Concurrent.apply[IO]

  "UserAlgSpec" should {

    """find a user""" in {
      val userAuth         = UserAuthAlgebra.async(async, transactor)
      val userAlgebr       = UserAlgebra.async(async, transactor)
      implicit val authCtx = userAuth.authenticate(email, userPw).unsafeRunSync()

      val foundUser = userAlgebr.findUser(user.id)

      foundUser.unsafeRunSync() match {
        case Some(User(_, userEmail, _)) =>
          userEmail mustEqual email
        case n => n mustEqual None
      }
    }
  }
}
