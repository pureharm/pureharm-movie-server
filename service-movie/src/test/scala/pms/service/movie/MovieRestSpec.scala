package pms.service.movie
import cats.effect.Timer
import doobie.util.transactor.Transactor
import pms.algebra.imdb.{IMDBAlgebraConfig, ModuleIMDBAsync}
import pms.algebra.movie.ModuleMovieAsync
import pms.algebra.user.ModuleUserAsync
import pms.db.config.{DatabaseConfig, DatabaseConfigAlgebra}
import pms.effects._
import pms.service.movie.rest.ModuleMovieRestAsync

import scala.concurrent.ExecutionContext

import org.http4s._
import org.http4s.implicits._

class MovieRestSpec
    extends ModuleMovieRestAsync[IO] with ModuleMovieServiceAsync[IO] with ModuleUserAsync[IO] with ModuleIMDBAsync[IO]
    with ModuleMovieAsync[IO] {

  val databaseConfig =
    DatabaseConfig("org.postgresql.Driver", "jdbc:postgresql:testmoviedatabase", "andrei", "qwerty", false)

  implicit val cs = IO.contextShift(ExecutionContext.global)

  implicit def transactor: Transactor[IO] = DatabaseConfigAlgebra.transactor(databaseConfig).unsafeRunSync()

  implicit def async: Async[IO] = Concurrent.apply[IO]

  implicit def concurrent: Concurrent[IO] = Concurrent.apply[IO]

  implicit def timer: Timer[IO] = Timer.apply[IO](IO.timer(ExecutionContext.global))

  override def imdbAlgebraConfig = IMDBAlgebraConfig.default[IO].unsafeRunSync()

  //movieModuleAuthedService.

  val success = ""

  val response: IO[Response[IO]] = movieModuleAuthedService. .orNotFound.run(
    Request(method = Method.GET, uri = Uri.uri("/user/not-used") )
  )
}
