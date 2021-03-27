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

import sbt.Keys._
import sbt._

object Settings {

  val common3Settings: Seq[Setting[_]] = Seq(
    scalaVersion := "3.0.0-RC1",
    testFrameworks += new TestFramework("munit.Framework"),
    scalacOptions ++= CompilerFlags.scala3Flags,
  )

  val common213Settings: Seq[Setting[_]] = Seq(
    //https://github.com/scala/scala/releases
    scalaVersion                       := "2.13.5",
    /*
     * Eliminates useless, unintuitive, and sometimes broken additions of `withFilter`
     * when using generator arrows in for comprehensions. e.g.
     *
     * Vanilla scala:
     * {{{
     *   for {
     *      x: Int <- readIntIO
     *      //
     *   } yield ()
     *   // instead of being `readIntIO.flatMap(x: Int => ...)`, it's something like .withFilter {case x: Int}, which is tantamount to
     *   // a runtime instanceof check. Absolutely horrible, and ridiculous, and unintuitive, and contrary to the often-
     *   // parroted mantra of "a for is just sugar for flatMap and map
     * }}}
     *
     * https://github.com/oleg-py/better-monadic-for
     */
    addCompilerPlugin("com.olegpy" %% "better-monadic-for" % "0.3.1"),
    /*
     * Kind projector brings a much needed feature to Scala, namely:
     * partially applied higher kinded types. For instance, if we
     * want to partially apply an Either[L, R], to fix the type
     * for L, we can't do that in vanilla scala (easily).
     *
     * But with kind projector we can simply do:
     * Either[*, R], which create an anonymous higher kinded
     * type which then takes the remaining type parameter
     * for the right hand side.
     *
     * https://github.com/typelevel/kind-projector
     */
    addCompilerPlugin(("org.typelevel" %% "kind-projector" % "0.11.3").cross(CrossVersion.full)),
    scalacOptions ++= CompilerFlags.scala2_13Flags ++ CompilerFlags.betterForPluginCompilerFlags,
    /** Required if we want to use munit as our testing framework.
      * https://scalameta.org/munit/docs/getting-started.html
      */
    testFrameworks += new TestFramework("munit.Framework"),
  )

}
