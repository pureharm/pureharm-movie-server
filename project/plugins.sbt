/**
  * The best thing since sliced bread.
  *
  * https://github.com/scalameta/scalafmt
  */
addSbtPlugin("com.geirsson" % "sbt-scalafmt" % "1.6.0-RC3")

/**
  * Refactoring/linting tool for scala.
  * Enable on per-use basis because it currently breaks a lot of IDEs
  *
  * https://github.com/scalacenter/scalafix
  * https://scalacenter.github.io/scalafix/
  *
  * From docs:
  * {{{
  *   // ===> sbt shell
  *
  *   > scalafixEnable                         // Setup scalafix for active session.
  *
  *   > scalafix                               // Run all rules configured in .scalafix.conf
  *
  *   > scalafix RemoveUnusedImports           // Run only RemoveUnusedImports rule
  *
  *   > myProject/scalafix RemoveUnusedImports // Run rule in one project only
  *
  * }}}
  */
addSbtPlugin("ch.epfl.scala" % "sbt-scalafix" % "0.6.0-M9")

/**
  * Used to create one big fat jar which contains all dependencies of this application
  *
  * https://github.com/sbt/sbt-assembly
  */
addSbtPlugin("com.eed3si9n" %% "sbt-assembly" % "0.14.7")

/**
  * neat way of visualizing the dependency graph both in the sbt repl, and to export
  * it as an .svg
  *
  * https://github.com/jrudolph/sbt-dependency-graph
  *
  */
addSbtPlugin("net.virtual-void" %% "sbt-dependency-graph" % "0.9.0")
