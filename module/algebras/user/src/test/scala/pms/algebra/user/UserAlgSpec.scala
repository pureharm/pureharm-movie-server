//package pms.algebra.user
//
//import doobie.util.dbPool.dbPool
//import pms.core.{Email,               Module, PlainTextPassword}
//import pms.db.config.{DatabaseConfig, DatabaseConfigAlgebra}
//import pms.core._
//
//import scala.concurrent.ExecutionContext
//
//class UserAlgSpec
//    extends org.specs2.mutable.Specification with Module[IO] with ModuleUserAlgebra[IO] with ModuleUserBootstrap[IO] {
//  implicit override def F: Concurrent[IO] = Concurrent.apply[IO]
//
//  val databaseConfig =
//    DatabaseConfig("org.postgresql.Driver", "jdbc:postgresql:testmoviedatabase", "busyuser", "qwerty", false)
//
//  implicit val cs = IO.contextShift(ExecutionContext.global)
//  implicit def dbPool: dbPool[IO] = DatabaseConfigAlgebra.dbPool(databaseConfig).unsafeRunSync()
//  implicit val userAccount      = userBootstrapAlgebra.unsafeRunSync()
//
//  DatabaseConfigAlgebra.initializeSQLDb(databaseConfig).unsafeRunSync()
//
//  val (user: User, email: Email, userRole: UserRole, userPw: PlainTextPassword) =
//    TestHelpersFunctions.unsafeCreateUser("SuperAdmin@yahoo.com", "SuperAdmin", "password1234")
//
//  "UserAlgSpec" should {
//
//    """find a user""" in {
//      val userAuth         = userAuthAlgebra.unsafeRunSync()
//      val userAlgebr       = userAlgebra.unsafeRunSync()
//      implicit val authCtx = userAuth.authenticate(email, userPw).unsafeRunSync()
//
//      val foundUser = userAlgebr.findUser(user.id)
//
//      foundUser.unsafeRunSync() match {
//        case Some(User(_, userEmail, _)) =>
//          userEmail mustEqual email
//        case n => n mustEqual None
//      }
//    }
//  }
//}
