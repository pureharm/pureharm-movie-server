//package pms.algebra.user
//
//import cats.effect.ContextShift
//import doobie.util.transactor.Transactor
//import pms.core.{Email, Module, PlainTextPassword}
//import pms.core._
//import pms.db.config.{DatabaseConfig, DatabaseConfigAlgebra}
//
//import scala.concurrent.ExecutionContext
//
//class UserAccountAlgSpec
//    extends org.specs2.mutable.Specification with Module[IO] with ModuleUserAlgebra[IO] with ModuleUserBootstrap[IO] {
//
//  val databaseConfig =
//    DatabaseConfig("org.postgresql.Driver", "jdbc:postgresql:testmoviedatabase", "busyuser", "qwerty", false)
//
//  implicit override def F: Concurrent[IO]   = Concurrent.apply[IO]
//  implicit val cs:         ContextShift[IO] = IO.contextShift(ExecutionContext.global)
//
//  implicit val transactor: Transactor[IO] = DatabaseConfigAlgebra.transactor(databaseConfig).unsafeRunSync()
//
//  implicit val userAccount = userModuleAlgebra.unsafeRunSync()
//  implicit val userBootstrapAlg: UserAccountBootstrapAlgebra[IO] =
//    UserAccountBootstrapAlgebra.impl(userAccount).unsafeRunSync()
//
//  DatabaseConfigAlgebra.initializeSQLDb(databaseConfig).unsafeRunSync()
//
//  val (user: User, email: Email, userRole: UserRole, userPw: PlainTextPassword) =
//    TestHelpersFunctions.unsafeCreateUser("SuperAdmin@yahoo.com", "SuperAdmin", "password1234")
//
//  "UserAccountAlgebra" should {
//
//    """register a user""" in {
//      user.email mustEqual email
//    }
//  }
//}
