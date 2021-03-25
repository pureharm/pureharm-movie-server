/*
 * Copyright 2021 BusyMachines
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package phms

import com.comcast.ip4s.{Host, Port}

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

  def env(name: EnvVar): ConfigValue[Effect, String] = ciris.env(name.show)

  def default[A](value: => A): ConfigValue[Effect, A] = ciris.default(value)

  implicit def defaultCirisEnvVarNewTypeConfigDecoder[O, N](implicit
    nt: NewType[O, N],
    ev: ConfigDecoder[String, O],
  ): ConfigDecoder[String, N] = ev.map(nt.newType)

  implicit def defaultCirisEnvVarRefinedTypeConfigDecoder[O, N](implicit
    nt: RefinedTypeThrow[O, N],
    ev: ConfigDecoder[String, O],
  ): ConfigDecoder[String, N] = ev.sproutRefined[N]

  implicit class Ops[O](v: EnvDecoder[O]) {
    def sprout[N](implicit s: NewType[O, N]): EnvDecoder[N] = v.map(s.newType)

    def sproutRefined[N](implicit s: RefinedTypeThrow[O, N]): EnvDecoder[N] =
      v.mapEither { (cfgKey, value) =>
        s.newType[Attempt](value).leftMap(thr => ciris.ConfigError(s"${thr.toString} --> $cfgKey"))
      }
  }

  implicit val portDecoder: ConfigDecoder[String, Port] =
    ConfigDecoder[String, Int].mapOption("Port")(Port.fromInt)

  implicit val hostDecoder: ConfigDecoder[String, Host] =
    ConfigDecoder[String, String].mapOption("Host")(Host.fromString)
}
