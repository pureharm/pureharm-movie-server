package pms.http

import fs2.Chunk
import io.circe.Printer
import pms._
import pms.json._
import org.http4s._
import org.http4s.headers.`Content-Type`
import org.http4s.{EntityDecoder, EntityEncoder}
import org.http4s.circe.CirceInstances

/** You need to have this in scope if you want "seamless" serializing/deserializing
  * to/from JSON in your HttpRoutes endpoints.
  *
  * @author Lorand Szakacs, https://github.com/lorandszakacs
  * @since 26 Jun 2018
  */
trait Http4sCirceInstances {

  import Http4sCirceInstances._

  /** This code was copied from [[org.http4s.circe.CirceInstances#jsonEncoderWithPrinter]]
    * Ideally, we would have done directly:
    * {{{
    *   circeInstance.jsonEncoderOf[F, T]
    * }}}
    * But that throws us into an infinit loop because the implicit picks itself up.
    *
    * @return
    */
  implicit def syncEntityJsonEncoder[F[_], T: Encoder]: EntityEncoder[F, T] = throw Fail.nicata("syncEntityJsonEncoder")
//    EntityEncoder[F, Chunk[Byte]]
//      .contramap[Json] { json =>
//        val bytes = printer.printToByteBuffer(json)
//        Chunk.byteBuffer(bytes)
//      }
//      .withContentType(`Content-Type`(MediaType.application.json))
//      .contramap(t => Encoder.apply[T].apply(t))

  implicit def syncEntityJsonDecoder[F[_]: Sync, T: Decoder]: EntityDecoder[F, T] =
    throw Fail.nicata("syncEntityJsonDecoder")
//    circeInstances.jsonOf[F, T]

}

object Http4sCirceInstances {
  private val printer: Printer = Printer.noSpaces.copy(dropNullValues = true)

  private val circeInstances: CirceInstances =
    CirceInstances.withPrinter(printer).build
}
