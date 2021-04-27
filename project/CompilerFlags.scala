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

object CompilerFlags {

  // format: off
  /**
    * See for reference
    * https://github.com/lampepfl/dotty/blob/master/compiler/src/dotty/tools/dotc/config/ScalaSettings.scala
    */
  def scala3Flags: Seq[String] = Seq(
    "-source:future-migration",           // allows us to use Scala 3 syntax, while still holding on to stuff that's hard to migrate, implicits from libraries and package objects
    "-deprecation",                       // deprecation warnings
    "-language:implicitConversions",      // enables old style of extension syntax, still used by some of our libraries, so we can't remove it they do a full rewrite with Scala 3 features
    "-language:higherKinds",              // can't do pure FP without this :)
    "-language:existentials",             // Existential types (besides wildcard types) can be written and inferred
    "-Ykind-projector",                   // replacement for the kind-projector compiler plugin we used in Scala 2
  )
}
