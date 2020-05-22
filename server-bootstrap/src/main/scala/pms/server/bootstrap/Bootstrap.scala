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
    def email[F[_]: MonadAttempt]: F[Email]             = Email("murray.bookchin@socialecology.fecund").liftTo[F]
    def passw[F[_]: MonadAttempt]: F[PlainTextPassword] = PlainTextPassword("OldManYellsAtAnarchism").liftTo[F]

    //the concatenated base64($email:$passw) string, on hand, in case needed
    val BasicAuthEncoding: String =
      "bXVycmF5LmJvb2tjaGluQHNvY2lhbGVjb2xvZ3kuZmVjdW5kOk9sZE1hblllbGxzQXRBbmFyY2hpc20="
  }

  def bootstrap[F[_]: Concurrent](usb: ServerBootstrapAlgebra[F]): F[Unit] = for {
    email <- superAdmin.email[F]
    passw <- superAdmin.passw[F]
    _     <- usb.bootStrapSuperAdmin(email, passw).void
  } yield ()

}
