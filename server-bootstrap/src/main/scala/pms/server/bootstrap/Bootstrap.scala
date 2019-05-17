package pms.server.bootstrap

import pms.core._
import pms.effects._
import pms.effects.implicits._

/**
  *
  * @author Lorand Szakacs, https://github.com/lorandszakacs
  * @since 13 Jul 2018
  *
  */
object Bootstrap {

  object superAdmin {
    val email: Email             = Email("murray.bookchin@socialecology.fecund").right.get
    val passw: PlainTextPassword = PlainTextPassword("OldManYellsAtAnarchism").right.get

    //the concatenated base64($email:$passw) string, on hand, in case needed
    val BasicAuthEncoding: String =
      "bXVycmF5LmJvb2tjaGluQHNvY2lhbGVjb2xvZ3kuZmVjdW5kOk9sZE1hblllbGxzQXRBbmFyY2hpc20="
  }

  def bootstrap[F[_]: Concurrent](usb: ServerBootstrapAlgebra[F]): F[Unit] =
    usb.bootStrapSuperAdmin(superAdmin.email, superAdmin.passw).void

}
