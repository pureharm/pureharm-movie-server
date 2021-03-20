package pms

package object config {
  final type Effect[A] = ciris.Effect[A]

  type ConfigDecoder[A, B] = ciris.ConfigDecoder[A, B]
  val ConfigDecoder: ciris.ConfigDecoder.type = ciris.ConfigDecoder

  type EnvDecoder[A] = ConfigDecoder[String, A]

  object EnvDecoder {
    def apply[A](implicit i: EnvDecoder[A]): EnvDecoder[A] = i
  }

  type ConfigValue[+F[_], A] = ciris.ConfigValue[F, A]
  val ConfigValue: ciris.ConfigValue.type = ciris.ConfigValue

  def env(name: String): ConfigValue[Effect, String] = ciris.env(name)

  def default[A](value: => A): ConfigValue[Effect, A] = ciris.default(value)

  implicit def defaultStringSproutForCiris[N](implicit
    ev: ConfigDecoder[String, String],
    nt: NewType[String, N],
  ): ConfigDecoder[String, N] = ev.map(nt.newType)

  implicit def defaultIntSproutForCiris[N](implicit
    ev: ConfigDecoder[String, Int],
    nt: NewType[Int, N],
  ): ConfigDecoder[String, N] = ev.map(nt.newType)

  implicit def defaultBooleanSproutForCiris[N](implicit
    ev: ConfigDecoder[String, Boolean],
    nt: NewType[Boolean, N],
  ): ConfigDecoder[String, N] = ev.map(nt.newType)

  implicit def defaultStringRefinedForCiris[N](implicit
    ev: ConfigDecoder[String, String],
    nt: RefinedTypeThrow[String, N],
  ): ConfigDecoder[String, N] = ev.sproutRefined[N]

  implicit def defaultIntRefinedForCiris[N](implicit
    ev: ConfigDecoder[String, Int],
    nt: RefinedTypeThrow[Int, N],
  ): ConfigDecoder[String, N] = ev.sproutRefined[N]

  implicit class Ops[O](v: EnvDecoder[O]) {
    def sprout[N](implicit s: NewType[O, N]): EnvDecoder[N] = v.map(s.newType)

    def sproutRefined[N](implicit s: RefinedTypeThrow[O, N]): EnvDecoder[N] =
      v.mapEither { (cfgKey, value) =>
        s.newType[Attempt](value).leftMap(thr => ciris.ConfigError(s"${thr.toString} --> $cfgKey"))
      }
  }
}
