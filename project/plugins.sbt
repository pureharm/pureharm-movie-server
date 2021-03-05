//=============================================================================
//=============================================================================

/**
  * The best thing since sliced bread.
  *
  * https://github.com/scalameta/scalafmt
  */
addSbtPlugin("org.scalameta" % "sbt-scalafmt" % "2.4.2") //https://github.com/scalameta/sbt-scalafmt/releases

//=============================================================================
//=============================================================================

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
  *   > scalafix RemoveUnused                  // Run only RemoveUnused rule
  *
  *   > myProject/scalafix RemoveUnused // Run rule in one project only
  *
  * }}}
  */
//addSbtPlugin("ch.epfl.scala" % "sbt-scalafix" % "0.9.5")

//=============================================================================
//=============================================================================

/**
  * Used to create the convenient executable that allows us
  * to easily run the entire project from the command line.
  *
  * https://github.com/sbt/sbt-native-packager/releases
  */
addSbtPlugin("com.typesafe.sbt" % "sbt-native-packager" % "1.8.0")

/** https://github.com/lampepfl/dotty/releases
  */
addSbtPlugin("ch.epfl.lamp" % "sbt-dotty" % "0.5.3")