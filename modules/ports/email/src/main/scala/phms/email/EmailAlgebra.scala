package phms.email

import cats.effect.std.Supervisor
import phms._
import phms.kernel._
import phms.logger._

/** This style of writing algebras (in layman terms: interface) is called
  * "final tagless".
  *
  * @author Lorand Szakacs, https://github.com/lorandszakacs
  * @since 05 Jun 2018
  */
trait EmailAlgebra[F[_]] {

  /** @param onCompletion
    * defines what to do based on the outcome of the email
    * @return
    */
  def sendEmail(to: Email, subject: Subject, content: Content)(
    onCompletion:   PartialFunction[OutcomeBackground[F], F[Unit]]
  ): F[Background[F]]
}

object EmailAlgebra {

  def resource[F[_]](
    config:        GmailConfig
  )(implicit sync: Sync[F], supervisor: Supervisor[F], logging: Logging[F]): Resource[F, EmailAlgebra[F]] =
    impl.EmailAlgebraJavaSync.resource[F](config).widen

}
