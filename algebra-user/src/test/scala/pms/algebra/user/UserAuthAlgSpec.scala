package pms.algebra.user

import busymachines.core.UnauthorizedFailure
import doobie.util.transactor.Transactor
import pms.core.{Email,               PlainTextPassword}
import pms.db.config.{DatabaseConfig, DatabaseConfigAlgebra}
import pms.effects._

import scala.concurrent.ExecutionContext

class UserAuthAlgSpec extends org.specs2.mutable.Specification with ModuleUserAsync[IO] with ModuleUserBootstrap[IO] {

  implicit val cs          = IO.contextShift(ExecutionContext.global)
  implicit val userAccount = UserAccountAlgebra.async(async, transactor)

  implicit def userBootstrapAlg = UserAccountBootstrapAlgebra.impl(userAccount)

  val (user: User, email: Email, userRole: UserRole, userPw: PlainTextPassword) =
    TestHelpersFunctions.createUser("SuperAdmin@yahoo.com", "SuperAdmin", "password1234")

  implicit def async: Async[IO] = Async.apply[IO]

  implicit def transactor: Transactor[IO] = DatabaseConfigAlgebra.transactor(databaseConfig).unsafeRunSync()

  val databaseConfig =
    DatabaseConfig("org.postgresql.Driver", "jdbc:postgresql:testmoviedatabase", "busyuser", "qwerty", true)

  DatabaseConfigAlgebra.initializeSQLDb(databaseConfig)

  //  val gmailConfig = GmailConfig(
  //    from     = "john.busylabs@gmail.com",
  //    user     = "john.busylabs@gmail.com",
  //    password = "]6|F;o2HPx/85-}BPgDo",
  //    host     = "smtp.gmail.com",
  //    port     = 587,
  //    auth     = true,
  //    startTLS = true
  //  )

  val userAuth   = UserAuthAlgebra.async(async, transactor)
  val userAlgebr = UserAlgebra.async(async,     transactor)

  // val emailAlgebra = EmailAlgebra.gmailClient(gmailConfig)(async)
  // val userService = UserAccountService.concurrent(userAuth, userAccount, userAlgebr, emailAlgebra)

  "UserAuthAlgebra" should {

    var authCtx: IO[AuthCtx] = IO.unit()

    """authenticate a user with email and password""" in {
      authCtx = userAuth.authenticate(email, userPw)
      val result = authCtx.unsafeRunSync()

      result.user mustEqual user
      result.token.isEmpty should_== (false)
    }

    """authenticate a user using the token""" in {
      val result = userAuth.authenticate(authCtx.unsafeRunSync().token)

      result.unsafeRunSync().user mustEqual user
    }

    """promote a user """ in {
      implicit val authCtx = userAuth.authenticate(email, userPw).unsafeRunSync()

      val (newbieUser, newbieEmail, newbieRole, newbiePw) =
        TestHelpersFunctions.createUser("newbie@yahoo.com", "Newbie", "password1234")

      val result = for {
        _    <- userAuth.promoteUser(newbieUser.id, UserRole.Member)
        user <- userAlgebr.findUser(newbieUser.id) ////!!!!daca user e Option[User] cum am ajuns cu result la IO[Option[User]]?

      } yield user

      result.unsafeRunSync() match {
        case Some(User(_, _, role)) => role mustEqual UserRole.Member
       // case _ => // not sure what to do
      }

    }

    """authorize a newbie to perform a certain operatin  """ in {
      val (newbieUser, newbieEmail, newbieRole, newbiePw) =
        TestHelpersFunctions.createUser("newbie_1@yahoo.com", "Newbie", "password1234")
      implicit val authCtx = userAuth.authenticate(newbieEmail, newbiePw).unsafeRunSync()

      def sum = (value: Int) => IO.pure(1 + value)

      val result = userAuth.authorize(sum(1))

      result.unsafeRunSync() should_== (2)

    }

    """authorize a member to perform a certain operatin && check that a newbie cant perfom that operation """ in {
      val (newbieUser, newbieEmail, newbieRole, newbiePw) =
        TestHelpersFunctions.createUser("newbie_2@yahoo.com", "Newbie", "password1234")

      val (memberUser, memberEmail, memberRole, memberPw) =
        TestHelpersFunctions.createUser("member@yahoo.com", "Member", "password1234")

      val authCtxMember = userAuth.authenticate(memberEmail, memberPw).unsafeRunSync()
      val authCtxNewbie = userAuth.authenticate(newbieEmail, newbiePw).unsafeRunSync()

      def sum = (value: Int) => IO.pure(1 + value)

      val resultMember = userAuth.authorizeMember(sum(1))(authCtxMember)
      val resultNewbie = userAuth.authorizeMember(sum(1))(authCtxNewbie)

      resultMember.unsafeRunSync() should_== (2)
      resultNewbie
        .unsafeRunSync() mustEqual UnauthorizedFailure("User not authorized to perform this action") //!!!not sure about this
    }

    """authorize a curator to perform a certain operatin  """ in {
      val (curatorUser, curatorEmail, curatorRole, curatorPw) =
        TestHelpersFunctions.createUser("curator@yahoo.com", "Curator", "password1234")

      implicit val authCtxNewbie = userAuth.authenticate(curatorEmail, curatorPw).unsafeRunSync()

      def sum = (value: Int) => IO.pure(1 + value)

      val result = userAuth.authorizeMember(sum(1))

      result.unsafeRunSync() should_== (2)
    }

    """authorize a SuperAdmin to perform a certain operatin  """ in {
      val (superUser, superEmail, superRole, superPw) =
        TestHelpersFunctions.createUser("super@yahoo.com", "SuperAdmin", "password1234")

      implicit val authCtxNewbie = userAuth.authenticate(superEmail, superPw).unsafeRunSync()

      def sum = (value: Int) => IO.pure(1 + value)

      val result = userAuth.authorizeMember(sum(1))

      result.unsafeRunSync() should_== (2)
    }
  }

}
