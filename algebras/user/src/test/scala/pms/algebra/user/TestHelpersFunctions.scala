//package pms.algebra.user
//
//import pms.core.{Email, PlainTextPassword}
//import pms.effects._
//
//object TestHelpersFunctions {
//
//  final def bootStrapUser(email: Email, pw: PlainTextPassword, role: UserRole)(
//    implicit userBootstrapAlg: UserAccountBootstrapAlgebra[IO],
//    userAccount: UserAccountAlgebra[IO]
//  ): IO[User] =
//    this.bootStrapUser(UserRegistration(email, pw, role))
//
//  final def bootStrapUser(
//                           reg: UserRegistration
//                         )(implicit userBootstrapAlg: UserAccountBootstrapAlgebra[IO], userAccount: UserAccountAlgebra[IO]): IO[User] =
//    for {
//      token <- userBootstrapAlg.bootstrapUser(reg)
//      user <- userAccount.registrationStep2(token)
//    } yield user
//
//  def createUser(email: String, role: String, password: String)(
//    implicit userBootstrapAlg: UserAccountBootstrapAlgebra[IO],
//    userAccount: UserAccountAlgebra[IO]
//  ): IO[(User, Email, UserRole, PlainTextPassword)] = {
//
//    val result = for {
//      userEmail <- Email(email)
//      userRole <- UserRole.fromName(role)
//      userPw <- PlainTextPassword(password)
//    } yield (userEmail, userRole, userPw)
//    for {
//      (userEmail, userRole, userPw) <- result.asIO
//      user <- bootStrapUser(userEmail, userPw, userRole)
//    } yield (user, userEmail, userRole, userPw)
//  }
//
//  def unsafeCreateUser(email: String, role: String, password: String)(
//    implicit userBootstrapAlg: UserAccountBootstrapAlgebra[IO],
//    userAccount: UserAccountAlgebra[IO]
//  ): (User, Email, UserRole, PlainTextPassword) = {
//    createUser(email, role, password).unsafeRunSync()
//  }
//}
