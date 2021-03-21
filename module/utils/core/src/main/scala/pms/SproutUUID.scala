package pms

trait SproutUUID extends Sprout[java.util.UUID] {

  def generate[F[_]](implicit F: Applicative[F], random: Random[F]): F[Type] =
    random.nextBytes(16).map(b => this.newType(java.util.UUID.nameUUIDFromBytes(b)))
}

trait SproutSubUUID extends SproutSub[java.util.UUID] {

  def generate[F[_]](implicit F: Applicative[F], random: Random[F]): F[Type] =
    random.nextBytes(16).map(b => this.newType(java.util.UUID.nameUUIDFromBytes(b)))
}
