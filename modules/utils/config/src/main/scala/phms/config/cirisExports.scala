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

package phms.config

import phms.*
import com.comcast.ip4s.{Host, Port}

final type Effect[A] = ciris.Effect[A]

type ConfigDecoder[A, B] = ciris.ConfigDecoder[A, B]
val ConfigDecoder: ciris.ConfigDecoder.type = ciris.ConfigDecoder

type ConfigValue[+F[_], A] = ciris.ConfigValue[F, A]
val ConfigValue: ciris.ConfigValue.type = ciris.ConfigValue

type EnvDecoder[A] = ConfigDecoder[String, A]
object EnvDecoder { def apply[A](using i: EnvDecoder[A]): EnvDecoder[A] = i }

def env(name: EnvVar): ConfigValue[Effect, String]  = ciris.env(name.show)
def default[A](value: => A): ConfigValue[Effect, A] = ciris.default(value)

implicit def defaultCirisEnvVarNewTypeConfigDecoder[O, N](using
  nt: NewType[O, N],
  ev: ConfigDecoder[String, O],
): ConfigDecoder[String, N] = ev.map(nt.newType)

implicit def defaultCirisEnvVarRefinedTypeConfigDecoder[O, N](using
  nt: RefinedTypeThrow[O, N],
  ev: ConfigDecoder[String, O],
): ConfigDecoder[String, N] = ev.sproutRefined[N]

extension [O](v: EnvDecoder[O]) {
  def sprout[N](using s: NewType[O, N]): EnvDecoder[N] = v.map(s.newType)
  def sproutRefined[N](using s: RefinedTypeThrow[O, N]): EnvDecoder[N] =
    v.mapEither { (cfgKey, value) =>
      s.newType[Attempt](value).leftMap(thr => ciris.ConfigError(s"${thr.toString} --> $cfgKey"))
    }
}

given portDecoder: ConfigDecoder[String, Port] = ConfigDecoder[String, Int].mapOption("Port")(Port.fromInt)
given hostDecoder: ConfigDecoder[String, Host] = ConfigDecoder[String, String].mapOption("Host")(Host.fromString)

