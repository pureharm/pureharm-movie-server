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
 * Used to create the convenient executable that allows us
 * to easily run the entire project from the command line.
 *
 * https://github.com/sbt/sbt-native-packager/releases
 */
addSbtPlugin("com.typesafe.sbt" % "sbt-native-packager" % "1.8.0")

//=============================================================================
//=============================================================================

/** https://github.com/lampepfl/dotty/releases
 */
addSbtPlugin("ch.epfl.lamp" % "sbt-dotty" % "0.5.3")

//=============================================================================
//=============================================================================

/** 
 * adds the sbt task headerCreateAll that adds license headers to files
 * 
 * https://github.com/sbt/sbt-header/releases
 */
addSbtPlugin("de.heikoseeberger" % "sbt-header" % "5.6.0")
