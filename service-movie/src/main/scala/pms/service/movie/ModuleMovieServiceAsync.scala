package pms.service.movie

/**
  *
  * @author Lorand Szakacs, https://github.com/lorandszakacs
  * @since 27 Jun 2018
  *
  */
trait ModuleMovieServiceAsync[F[_]] {

  def imdbService: IMDBService[F] = ???

}
