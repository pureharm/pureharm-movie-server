package pms.algebra.user

import doobie.util.transactor.Transactor
import pms.core.{Email,               PlainTextPassword}
import pms.db.config.{DatabaseConfig, DatabaseConfigAlgebra}
import pms.effects._

import scala.concurrent.ExecutionContext

class UserAuthAlgSpec extends org.specs2.mutable.Specification with ModuleUserAsync[IO] with ModuleUserBootstrap[IO] {

  val databaseConfig =
    DatabaseConfig("org.postgresql.Driver", "jdbc:postgresql:testmoviedatabase", "busyuser", "qwerty", false)

  implicit val cs = IO.contextShift(ExecutionContext.global)
  implicit def transactor:       Transactor[IO]                  = DatabaseConfigAlgebra.transactor(databaseConfig).unsafeRunSync()
  implicit val userAccount:      UserAccountAlgebra[IO]          = UserAccountAlgebra.async(async, transactor)
  implicit def userBootstrapAlg: UserAccountBootstrapAlgebra[IO] = UserAccountBootstrapAlgebra.impl(userAccount)

  DatabaseConfigAlgebra.initializeSQLDb(databaseConfig).unsafeRunSync()

  val (user: User, email: Email, userRole: UserRole, userPw: PlainTextPassword) =
    TestHelpersFunctions.unsafeCreateUser("SuperAdmin@yahoo.com", "SuperAdmin", "password1234")

  implicit def async: Async[IO] = Concurrent.apply[IO]

  val userAuth   = UserAuthAlgebra.async(async, transactor)
  val userAlgebr = UserAlgebra.async(async,     transactor)

  "UserAuthAlgebra" should {

    """authenticate a user with email and password""" in {
      val authCtx = userAuth.authenticate(email, userPw)
      val result  = authCtx.unsafeRunSync()

      result.user mustEqual user
      result.token.isEmpty should_== (false)
    }

    """authenticate a user using the token""" in {
      val authCtx = userAuth.authenticate(email, userPw)
      val result  = userAuth.authenticate(authCtx.unsafeRunSync().token)

      result.unsafeRunSync().user mustEqual user
    }

    """promote a user """ in {
      implicit val authCtx = userAuth.authenticate(email, userPw).unsafeRunSync()

      val (newbieUser, _, _, _) =
        TestHelpersFunctions.unsafeCreateUser("newbie@yahoo.com", "Newbie", "password1234")

      val result = for {
        _    <- userAuth.promoteUser(newbieUser.id, UserRole.Member)
        user <- userAlgebr.findUser(newbieUser.id)

      } yield user

      result.unsafeRunSync() match {
        case Some(User(_, _, role)) => role mustEqual UserRole.Member
        case n => n mustEqual None
      }

    }

    """authorize a newbie to perform a certain operatin  """ in {
      val (_, newbieEmail, _, newbiePw) =
        TestHelpersFunctions.unsafeCreateUser("newbie_1@yahoo.com", "Newbie", "password1234")
      implicit val authCtx = userAuth.authenticate(newbieEmail, newbiePw).unsafeRunSync()

      def sum = (value: Int) => IO.pure(1 + value)

      val result = userAuth.authorize(sum(1))

      result.unsafeRunSync() should_== (2)

    }

    """authorize a member to perform a certain operatin """ in {

      val (_, memberEmail, _, memberPw) =
        TestHelpersFunctions.unsafeCreateUser("member@yahoo.com", "Member", "password1234")

      val authCtxMember = userAuth.authenticate(memberEmail, memberPw).unsafeRunSync()

      def sum = (value: Int) => IO.pure(1 + value)

      val resultMember = userAuth.authorizeMember(sum(1))(authCtxMember)

      resultMember.unsafeRunSync() should_== (2)

    }

    """authorize a curator to perform a certain operatin  """ in {
      val (_, curatorEmail, _, curatorPw) =
        TestHelpersFunctions.unsafeCreateUser("curator@yahoo.com", "Curator", "password1234")

      implicit val authCtxNewbie = userAuth.authenticate(curatorEmail, curatorPw).unsafeRunSync()

      def sum = (value: Int) => IO.pure(1 + value)

      val result = userAuth.authorizeMember(sum(1))

      result.unsafeRunSync() should_== (2)
    }

    """authorize a SuperAdmin to perform a certain operatin  """ in {
      val (_, superEmail, _, superPw) =
        TestHelpersFunctions.unsafeCreateUser("super@yahoo.com", "SuperAdmin", "password1234")

      implicit val authCtxNewbie = userAuth.authenticate(superEmail, superPw).unsafeRunSync()

      def sum = (value: Int) => IO.pure(1 + value)

      val result = userAuth.authorizeMember(sum(1))

      result.unsafeRunSync() should_== (2)
    }
  }
}
