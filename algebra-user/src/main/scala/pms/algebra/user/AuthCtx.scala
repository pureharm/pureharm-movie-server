package pms.algebra.user

/**
  *
  * @author Lorand Szakacs, https://github.com/lorandszakacs
  * @since 20 Jun 2018
  *
  */
final case class AuthCtx(
  token: AuthenticationToken,
  user:  User,
)
