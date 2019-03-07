package pms.algebra.user

import pms.core.{Email, PlainTextPassword}
import pms.effects._

object TestHelpersFunctions {

  final def bootStrapUser(email: Email, pw: PlainTextPassword, role: UserRole)(
    implicit userBootstrapAlg:   UserAccountBootstrapAlgebra[IO],
    userAccount:                 UserAccountAlgebra[IO]
  ): IO[User] =
    this.bootStrapUser(UserRegistration(email, pw, role))

  final def bootStrapUser(
    reg:                       UserRegistration
  )(implicit userBootstrapAlg: UserAccountBootstrapAlgebra[IO], userAccount: UserAccountAlgebra[IO]): IO[User] =
    for {
      token <- userBootstrapAlg.bootstrapUser(reg)
      user  <- userAccount.registrationStep2(token)
    } yield user

  def createUser(email:        String, role: String, userPw: String)(
    implicit userBootstrapAlg: UserAccountBootstrapAlgebra[IO],
    userAccount:               UserAccountAlgebra[IO]
  ): (User, Email, UserRole, PlainTextPassword) = {
    val (user: User, email: Email, userRole: UserRole, userPw: PlainTextPassword) = (for {
      email    <- Email(email)
      userRole <- UserRole.fromName(role)
      userPw   <- PlainTextPassword(userPw)
      user     <- bootStrapUser(email, userPw, userRole)
    } yield (user, email, userRole, userPw)).unsafeGet()

    (user, email, userRole, userPw)
  }
}
