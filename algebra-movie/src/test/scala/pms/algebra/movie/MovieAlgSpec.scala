package pms.algebra.movie

import java.time.LocalDate

import pms.core.PlainTextPassword
import pms.effects._
import pms.email.{EmailAlgebra, GmailConfig}
import pms.server.bootstrap.ModuleServerBootstrap
import pms.db.config._
import doobie.util.transactor.Transactor
import pms.algebra.user._
import pms.core.Email

import scala.concurrent.ExecutionContext
import doobie._
import doobie.implicits._
import pms.service.user.UserAccountService
import spire.math._

class MovieAlgSpec
    extends org.specs2.mutable.Specification with ModuleMovieAsync[IO] with ModuleServerBootstrap[IO]
    with ModuleUserAsync[IO] with ModuleUserBootstrap[IO] { //with ModuleMovieAsync[IO]

  implicit val cs = IO.contextShift(ExecutionContext.global)

  val (user: User, email, userPw, transact) = (for {
    email    <- Email("andrei@yahoo.com")
    userRole <- UserRole.fromName("Curator")
    userPw   <- PlainTextPassword("password1234")
    user     <- serverBootstrapAlgebra.bootStrapUser(email, userPw, userRole)
    transact <- DatabaseConfigAlgebra.transactor(databaseConfig)
  } yield (user, email, userPw, transact)).unsafeGet()

  implicit def async: Async[IO] = Async.apply[IO]

  //  implicit override def transactor = Transactor.fromDriverManager[IO](
  //    "org.postgresql.Driver", // driver classname
  //    "jdbc:postgresql:world", // connect URL (driver-specific)
  //    "postgres", // user
  //    "" // password
  //  )

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

  val userAuth = UserAuthAlgebra.async(async, transactor)
  // val userAccount  = UserAccountAlgebra.async(async, transactor)
  // val userAlgebr   = UserAlgebra.async(async, transactor)
  // val emailAlgebra = EmailAlgebra.gmailClient(gmailConfig)(async)

  // val userService = UserAccountService.concurrent(userAuth, userAccount, userAlgebr, emailAlgebra)

 implicit val authCtx = userAuth.authenticate(email,userPw).unsafeRunSync()

//  implicit val autCtx: AuthCtx =
//    AuthCtx(AuthenticationToken("Token"), User(user.id, user.email, user.role))

  "MovieAlgebra" should {

    """create a movie""" in {
      val movie = MovieCreation(MovieTitle("Title"), Option(ReleaseDate(LocalDate.now())))

      val result = movieAlgebra.createMovie(movie)

      result.unsafeRunSync() mustEqual movie
    }

    """find all movies in an interval""" in {

      val intervalQuery: QueryInterval = Interval(ReleaseDate(LocalDate.of(2000, 7, 12)), ReleaseDate(LocalDate.now()))
      val result = movieAlgebra.findMoviesBetween(intervalQuery)

      result.unsafeRunSync().isEmpty should_== (true)

    }
  }
}
