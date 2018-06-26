package pms.http

import org.http4s._
import pms.effects._
import pms.json._

/**
  *
  * @author Lorand Szakacs, https://github.com/lorandszakacs
  * @since 26 Jun 2018
  *
  */
trait CirceToHttp4sEncoders {

  //FIXME: move to commons core
  implicit def asyncEntityJsonEncoder[F[_], T](implicit codec: Encoder[T], async: Async[F]): EntityEncoder[F, T] =
    ???

  implicit def asyncEntityJsonDecoder[F[_], T](implicit codec: Decoder[T], async: Async[F]): EntityDecoder[F, T] =
    ???

}
