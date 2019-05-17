package pms.algebra.user

import pms.core._
import pms.effects._
import pms.effects.implicits._
import busymachines.core.UnauthorizedFailure

/**
  *
  * @author Lorand Szakacs, https://github.com/lorandszakacs
  * @since 20 Jun 2018
  *
  */
abstract class UserAuthAlgebra[F[_]](implicit private val monadError: MonadError[F, Throwable]) {

  def authenticate(email: Email, pw: PlainTextPassword): F[AuthCtx]

  def authenticate(token: AuthenticationToken): F[AuthCtx]

  final def promoteUser(id: UserID, newRole: UserRole)(implicit auth: AuthCtx): F[Unit] =
    authorizeGTERole(newRole)(promoteUserOP(id, newRole))

  /**
    * Lowest level of authorization, essentially anyone
    * who is logged in can perform the given op.
    *
    * @param op
    *   The operation that we want to guard with
    *   certain user priviliges.
    */
  final def authorizeNewbie[A](op: => F[A])(implicit auth: AuthCtx): F[A] =
    authorizeGTERole(UserRole.Newbie)(op)

  /**
    * Requires member to have priviliges from [[UserRole.Member]] upwards
    *
    * @param op
    *   The operation that we want to guard with
    *   certain user priviliges.
    */
  final def authorizeMember[A](op: => F[A])(implicit auth: AuthCtx): F[A] =
    authorizeGTERole(UserRole.Member)(op)

  /**
    * Requires member to have priviliges from [[UserRole.Curator]] upwards
    *
    * @param op
    *   The operation that we want to guard with
    *   certain user priviliges.
    */
  final def authorizeCurator[A](op: => F[A])(implicit auth: AuthCtx): F[A] =
    authorizeGTERole(UserRole.Curator)(op)

  /**
    * Requires member to have priviliges from [[UserRole.SuperAdmin]] upwards
    *
    * @param op
    *   The operation that we want to guard with
    *   certain user priviliges.
    */
  final def authorizeSuperAdmin[A](op: => F[A])(implicit auth: AuthCtx): F[A] =
    authorizeGTERole(UserRole.SuperAdmin)(op)

  protected[user] def promoteUserOP(id: UserID, newRole: UserRole): F[Unit]

  final protected[user] def authorizeGTERole[A](minRole: UserRole)(op: => F[A])(implicit auth: AuthCtx): F[A] =
    for {
      _ <- if (auth.user.role >= minRole) op
      else
        monadError.raiseError(UnauthorizedFailure("User not authorized to perform this action"))
      result <- op
    } yield result
}
