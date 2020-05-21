package pms.service.movie.rest

import java.time.LocalDate

import pms.json._
import pms.algebra.movie._

/**
  *
  * @author Lorand Szakacs, https://github.com/lorandszakacs
  * @since 25 Jun 2018
  *
  */
object MovieRoutesJSON extends MovieRoutesJSON

trait MovieRoutesJSON extends PMSJson {

  //TODO: remove boilerplate
  implicit val movieIDCirceCodec:     Codec[MovieID]     = Codec.from(
    Decoder.apply[Long].map(MovieID.apply),
    Encoder.apply[Long].contramap(m => MovieID.despook(m)),
  )

  //TODO: remove boilerplate
  implicit val movieTitleCirceCodec:  Codec[MovieTitle]  = Codec.from(
    Decoder.apply[String].map(MovieTitle.apply),
    Encoder.apply[String].contramap(m => MovieTitle.despook(m)),
  )

  //TODO: remove boilerplate
  implicit val releaseDateCirceCodec: Codec[ReleaseDate] = Codec.from(
    Decoder.apply[LocalDate].map(ReleaseDate.apply),
    Encoder.apply[LocalDate].contramap(m => ReleaseDate.despook(m)),
  )

  implicit val movieCirceCodec: Codec[Movie] = derive.codec[Movie]

  implicit val movieCreationCirceCodec: Codec[MovieCreation] =
    derive.codec[MovieCreation]
}
