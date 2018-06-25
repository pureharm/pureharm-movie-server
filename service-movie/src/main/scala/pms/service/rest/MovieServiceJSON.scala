package pms.service.rest

import java.time.LocalDate

import org.http4s._

import pms.effects._
import pms.json._
import pms.algebra.movie._


/**
  *
  * @author Lorand Szakacs, https://github.com/lorandszakacs
  * @since 25 Jun 2018
  *
  */
object MovieServiceJSON extends MovieServiceJSON

trait MovieServiceJSON extends PMSJson {

  //TODO: remove boilerplate
  implicit val movieIDCirceCodec: Codec[MovieID] = Codec.instance(
    Encoder.apply[Long].contramap(m => MovieID.exorcise(m)),
    Decoder.apply[Long].map(MovieID.apply)
  )

  //TODO: remove boilerplate
  implicit val movieTitleCirceCodec: Codec[MovieTitle] = Codec.instance(
    Encoder.apply[String].contramap(m => MovieTitle.exorcise(m)),
    Decoder.apply[String].map(MovieTitle.apply)
  )

  //TODO: remove boilerplate
  implicit val releaseDateCirceCodec: Codec[ReleaseDate] = Codec.instance(
    Encoder.apply[LocalDate].contramap(m => ReleaseDate.exorcise(m)),
    Decoder.apply[LocalDate].map(ReleaseDate.apply)
  )

  implicit val movieCirceCodec: Codec[Movie] = derive.codec[Movie]

  implicit val movieCreationCirceCodec: Codec[MovieCreation] = derive.codec[MovieCreation]

  implicit def asyncEntityJsonEncoder[F[_], T](implicit codec: Encoder[T], async: Async[F]): EntityEncoder[F, T] =
    ???

  implicit def asyncEntityJsonDecoder[F[_], T](implicit codec: Decoder[T], async: Async[F]): EntityDecoder[F, T] =
    ???
}
