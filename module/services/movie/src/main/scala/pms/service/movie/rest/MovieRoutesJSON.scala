package pms.service.movie.rest

import pms.json._
import pms.json.implicits._
import pms.algebra.movie._

/**
  *
  * @author Lorand Szakacs, https://github.com/lorandszakacs
  * @since 25 Jun 2018
  *
  */
object MovieRoutesJSON extends MovieRoutesJSON

trait MovieRoutesJSON  {

  implicit val movieCirceCodec: Codec[Movie] = derive.codec[Movie]

  implicit val movieCreationCirceCodec: Codec[MovieCreation] =
    derive.codec[MovieCreation]
}
