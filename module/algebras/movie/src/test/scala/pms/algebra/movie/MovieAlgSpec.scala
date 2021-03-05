//package pms.algebra.movie
//
//import java.time.LocalDate
//
//import pms.core.{Email, Module, PlainTextPassword}
//import pms.core._
//import pms.server.bootstrap.ModuleServerBootstrap
//import pms.db.config._
//import doobie.util.transactor.Transactor
//import pms.algebra.user._
//import spire.math.Interval
//
//import scala.concurrent.ExecutionContext
//
//class MovieAlgSpec
//    extends org.specs2.mutable.Specification with Module[IO] with ModuleMovieAlgebra[IO] with ModuleServerBootstrap[IO]
//    with ModuleUserAlgebra[IO] with ModuleUserBootstrap[IO] {
//
//  implicit def F: Concurrent[IO] = Concurrent.apply[IO]
//
//  val databaseConfig =
//    DatabaseConfig("org.postgresql.Driver", "jdbc:postgresql:testmoviedatabase", "busyuser", "qwerty", false)
//
//  implicit val cs = IO.contextShift(ExecutionContext.global)
//  implicit val transactor:       Transactor[IO]                  = DatabaseConfigAlgebra.transactor(databaseConfig).unsafeRunSync()
//  implicit val userAccount:      UserAccountAlgebra[IO]          = UserAccountAlgebra.async(F, transactor)
//  implicit val userBootstrapAlg: UserAccountBootstrapAlgebra[IO] = UserAccountBootstrapAlgebra.impl(userAccount)
//
//  DatabaseConfigAlgebra.initializeSQLDb(databaseConfig).unsafeRunSync()
//
//  val (user: User, email: Email, userRole: UserRole, userPw: PlainTextPassword) =
//    TestHelpersFunctions.unsafeCreateUser("SuperAdmin_Movie@yahoo.com", "SuperAdmin", "password1234")
//
//  val userAuth = UserAuthAlgebra.async(F, transactor)
//
//  implicit val authCtx = userAuth.authenticate(email, userPw).unsafeRunSync()
//
//  "MovieAlgebra" should {
//
//    """create a movie""" in {
//      val movie = MovieCreation(MovieTitle("Title"), Option(ReleaseDate(LocalDate.now())))
//
//      val result = movieAlgebra.unsafeRunSync().createMovie(movie)
//
//      result.unsafeRunSync().name mustEqual movie.name
//    }
//
//    """find all movies in an interval""" in {
//
//      val intervalQuery: QueryInterval = Interval(ReleaseDate(LocalDate.of(2000, 7, 12)), ReleaseDate(LocalDate.now()))
//      val result = movieAlgebra.unsafeRunSync().findMoviesBetween(intervalQuery)
//
//      result.unsafeRunSync().isEmpty should_== (false)
//
//    }
//  }
//}
