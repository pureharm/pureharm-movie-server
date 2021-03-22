package phms.server.bootstrap

import phms._
import phms.kernel._

/** @author Lorand Szakacs, https://github.com/lorandszakacs
  * @since 13 Jul 2018
  */
object Bootstrap {

  object superAdmin {
    def email[F[_]: MonadThrow]: F[Email]             = Email[F]("murray.bookchin@socialecology.fecund")
    def passw[F[_]: MonadThrow]: F[PlainTextPassword] = PlainTextPassword[F]("OldManYellsAtAnarchism")

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
