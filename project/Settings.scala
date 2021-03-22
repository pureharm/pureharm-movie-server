import CompilerFlags._
import sbt.Keys._
import sbt._

object Settings {

  val commonSettings: Seq[Setting[_]] = Seq(
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
    scalacOptions ++= scala2_13Flags ++ betterForPluginCompilerFlags,
    /** Required if we want to use munit as our testing framework.
      * https://scalameta.org/munit/docs/getting-started.html
      */
    testFrameworks += new TestFramework("munit.Framework"),
  )

}
