package pms.algebra.user

import java.time.LocalDate

import doobie.util.transactor.Transactor
import pms.core.{Email,               PlainTextPassword}
import pms.db.config.{DatabaseConfig, DatabaseConfigAlgebra}
import pms.effects._
import pms.email.{EmailAlgebra, GmailConfig}

import scala.concurrent.ExecutionContext

class UserAuthAlgSpec extends org.specs2.mutable.Specification with ModuleUserAsync[IO] with ModuleUserBootstrap[IO] {

  implicit val cs = IO.contextShift(ExecutionContext.global)

  val (user: User, email: Email, userPw, transact) = (for {
    email    <- Email("andrei@yahoo.com")
    userRole <- UserRole.fromName("Curator")
    userPw   <- PlainTextPassword("password1234")
    user     <- bootStrapUser(email, userPw, userRole)
    transact <- DatabaseConfigAlgebra.transactor(databaseConfig)
  } yield (user, email, userPw, transact)).unsafeGet()

  implicit def async: Async[IO] = Async.apply[IO]

  val databaseConfig =
    DatabaseConfig("org.postgresql.Driver", "jdbc:postgresql:testmoviedatabase", "busyuser", "qwerty", true)

  implicit def transactor: Transactor[IO] = transact

  DatabaseConfigAlgebra.initializeSQLDb(databaseConfig)

  val gmailConfig = GmailConfig(
    from     = "john.busylabs@gmail.com",
    user     = "john.busylabs@gmail.com",
    password = "]6|F;o2HPx/85-}BPgDo",
    host     = "smtp.gmail.com",
    port     = 587,
    auth     = true,
    startTLS = true
  )

  val userAuth         = UserAuthAlgebra.async(async, transactor)
  val userAccount      = UserAccountAlgebra.async(async, transactor)
  val userBootstrapAlg = UserAccountBootstrapAlgebra.impl(userAccount)

  final def bootStrapUser(email: Email, pw: PlainTextPassword, role: UserRole): IO[User] =
    this.bootStrapUser(UserRegistration(email, pw, role))

  final def bootStrapUser(reg: UserRegistration): IO[User] =
    for {
      token <- userBootstrapAlg.bootstrapUser(reg)
      user  <- userAccount.registrationStep2(token)
    } yield user

  // val userAlgebr   = UserAlgebra.async(async, transactor)
  // val emailAlgebra = EmailAlgebra.gmailClient(gmailConfig)(async)
  // val userService = UserAccountService.concurrent(userAuth, userAccount, userAlgebr, emailAlgebra)

  "UserAccountAlgebra" should {

    """register a user""" in {
      user.email mustEqual email
    }
  }

  "UserAuthAlgebra" should {

    var authCtx: IO[AuthCtx] = IO.unit()

    """authenticate a user with eamil and password""" in {
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
      val result = userAuth.pr

      result.unsafeRunSync().user mustEqual user
    }
  }
}
