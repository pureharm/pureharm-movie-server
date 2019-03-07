package pms.algebra.user

import doobie.util.transactor.Transactor
import pms.core.{Email, PlainTextPassword}
import pms.db.config.{DatabaseConfig, DatabaseConfigAlgebra}
import pms.effects._
import scala.concurrent.ExecutionContext

class UserAccountAlgSpec extends org.specs2.mutable.Specification with ModuleUserAsync[IO] with ModuleUserBootstrap[IO] {
  implicit val cs = IO.contextShift(ExecutionContext.global)
  implicit val userAccount      = UserAccountAlgebra.async(async, transactor)
  implicit def userBootstrapAlg = UserAccountBootstrapAlgebra.impl(userAccount)

  val (user: User, email: Email, userRole: UserRole, userPw: PlainTextPassword) =
    TestHelpersFunctions.createUser("SuperAdmin@yahoo.com", "SuperAdmin", "password1234")

  implicit def async: Async[IO] = Async.apply[IO]

  implicit def transactor: Transactor[IO] = DatabaseConfigAlgebra.transactor(databaseConfig).unsafeRunSync()

  val databaseConfig =
    DatabaseConfig("org.postgresql.Driver", "jdbc:postgresql:testmoviedatabase", "busyuser", "qwerty", true)

  DatabaseConfigAlgebra.initializeSQLDb(databaseConfig)

  "UserAccountAlgebra" should {

    """register a user""" in {
      user.email mustEqual email
    }
  }
}
