package pms.rest.movie

import pms.algebra.movie._
import pms.json._

/** @author Lorand Szakacs, https://github.com/lorandszakacs
  * @since 25 Jun 2018
  */
object MovieRoutesJSON extends MovieRoutesJSON

trait MovieRoutesJSON {

  implicit val movieCirceCodec: Codec[Movie] = derive.codec[Movie]

  implicit val movieCreationCirceCodec: Codec[MovieCreation] =
    derive.codec[MovieCreation]
}
