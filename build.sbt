import sbt._
import Settings._

addCommandAlias("mkSite", ";docs/clean;docs/makeMicrosite")
addCommandAlias("doSitePublish", ";docs/clean;docs/publishMicrosite")
addCommandAlias("mkJar", ";clean;update;compile;server/assembly")

//=============================================================================
//=============================================================================
ThisBuild / version := "1.0.0"

lazy val server = Project(id = "server", file("./module/apps/phms"))
  .settings(commonSettings)
  .enablePlugins(JavaAppPackaging)
  .settings(
    Compile / mainClass := Option("phms.server.PureMovieServerApp")
  )
  .settings(
    libraryDependencies ++= Seq(
    )
  )
  .dependsOn(
    `phms-core`,
    `phms-logger`,
    `phms-config`,
    `phms-db-config`,
    `phms-http`,
    `phms-core`,
    `service-user`,
    `service-movie`,
    `rest-user`,
    `rest-movie`,
    `bootstrap`,
    asTestingLibrary(`phms-testkit`),
  )
  .aggregate(
    `phms-core`,
    `phms-logger`,
    `phms-config`,
    `phms-db-config`,
    `phms-http`,
    `phms-core`,
    `service-user`,
    `service-movie`,
    `rest-user`,
    `rest-movie`,
    `bootstrap`,
  )

lazy val `bootstrap` = project
  .settings(commonSettings)
  .settings(
    libraryDependencies ++= Seq(
    )
  )
  .dependsOn(
    `phms-logger`,
    `phms-core`,
    `phms-config`,
    `phms-core`,
    `phms-db-config`,
    `algebra-user`,
    asTestingLibrary(`phms-testkit`),
  )
  .aggregate(
    `phms-logger`,
    `phms-core`,
    `phms-config`,
    `phms-db-config`,
    `phms-core`,
    `algebra-user`,
  )

lazy val `service-user` = serviceProject("user")
  .settings(
    libraryDependencies ++= Seq(
    )
  )
  .dependsOn(
    `algebra-user`,
    `algebra-http-sec`,
    `phms-email`,
    `phms-config`,
    `phms-logger`,
    `phms-core`,
    `phms-core`,
    `phms-json`,
    `phms-http`,
    asTestingLibrary(`phms-testkit`),
  )
  .aggregate(
    `algebra-user`,
    `algebra-http-sec`,
    `phms-email`,
    `phms-config`,
    `phms-logger`,
    `phms-core`,
    `phms-core`,
    `phms-json`,
    `phms-http`,
  )

lazy val `service-movie` = serviceProject("movie")
  .settings(
    libraryDependencies ++= Seq(
    )
  )
  .dependsOn(
    `algebra-user`,
    `algebra-imdb`,
    `algebra-movie`,
    `algebra-http-sec`,
    `phms-config`,
    `phms-logger`,
    `phms-core`,
    `phms-core`,
    `phms-json`,
    `phms-http`,
    asTestingLibrary(`phms-testkit`),
  )
  .aggregate(
    `algebra-user`,
    `algebra-imdb`,
    `algebra-movie`,
    `algebra-http-sec`,
    `phms-config`,
    `phms-logger`,
    `phms-core`,
    `phms-core`,
    `phms-json`,
    `phms-http`,
  )

lazy val `rest-user` = restProject("user")
  .settings(
    libraryDependencies ++= Seq(
    )
  )
  .dependsOn(
    `algebra-user`,
    `service-user`,
    `algebra-http-sec`,
    `phms-email`,
    `phms-config`,
    `phms-logger`,
    `phms-core`,
    `phms-json`,
    `phms-http`,
    asTestingLibrary(`phms-testkit`),
  )
  .aggregate(
    `algebra-user`,
    `service-user`,
    `algebra-http-sec`,
    `phms-email`,
    `phms-config`,
    `phms-logger`,
    `phms-core`,
    `phms-json`,
    `phms-http`,
  )

lazy val `rest-movie` = restProject("movie")
  .settings(
    libraryDependencies ++= Seq(
    )
  )
  .dependsOn(
    `algebra-user`,
    `service-movie`,
    `algebra-movie`,
    `algebra-http-sec`,
    `phms-config`,
    `phms-logger`,
    `phms-core`,
    `phms-json`,
    `phms-http`,
    asTestingLibrary(`phms-testkit`),
  )
  .aggregate(
    `algebra-user`,
    `service-movie`,
    `algebra-movie`,
    `algebra-http-sec`,
    `phms-config`,
    `phms-logger`,
    `phms-core`,
    `phms-json`,
    `phms-http`,
  )

lazy val `algebra-http-sec` = algebraProject("http-sec")
  .settings(
    libraryDependencies ++= Seq(
    )
  )
  .dependsOn(
    `phms-config`,
    `phms-core`,
    `phms-http`,
    `algebra-user`,
    asTestingLibrary(`phms-testkit`),
  )
  .aggregate(
    `phms-config`,
    `phms-core`,
    `phms-http`,
    `algebra-user`,
  )

lazy val `algebra-imdb` = algebraProject("imdb")
  .settings(
    libraryDependencies ++= Seq(
      Libraries.scalaScrapper
    )
  )
  .dependsOn(
    `phms-config`,
    `phms-logger`,
    `phms-core`,
    asTestingLibrary(`phms-testkit`),
  )
  .aggregate(
    `phms-config`,
    `phms-logger`,
    `phms-core`,
  )

lazy val `algebra-movie` = algebraProject("movie")
  .settings(
    libraryDependencies ++= Seq(
    )
  )
  .dependsOn(
    `algebra-user`,
    `phms-config`,
    `phms-core`,
    `phms-db`,
    asTestingLibrary(`phms-testkit`),
  )
  .aggregate(
    `algebra-user`,
    `phms-config`,
    `phms-core`,
    `phms-db`,
  )

lazy val `algebra-user` = algebraProject("user")
  .settings(
    libraryDependencies ++= Seq(
    )
  )
  .dependsOn(
    `phms-config`,
    `phms-core`,
    `phms-email`,
    `phms-db`,
    `phms-crypto`,
    `phms-kernel`,
    `phms-time`,
    asTestingLibrary(`phms-testkit`),
  )
  .aggregate(
    `phms-config`,
    `phms-core`,
    `phms-email`,
    `phms-db`,
    `phms-crypto`,
    `phms-kernel`,
    `phms-time`,
  )

//=============================================================================
//=============================================================================
//=============================================================================

//=============================================================================
//=============================================================================
//=============================================================================

lazy val `phms-time` = utilProject("time")
  .settings(
    libraryDependencies ++= Seq(
      Libraries.scalaJavaTime
    )
  )
  .dependsOn(
    `phms-core`,
    asTestingLibrary(`phms-testkit`),
  )

lazy val `phms-db` = utilProject("db")
  .settings(
    libraryDependencies ++= Seq(
      Libraries.skunk
    )
  )
  .dependsOn(
    `phms-core`,
    `phms-db-config`,
    `phms-logger`,
    `phms-kernel`,
    asTestingLibrary(`phms-testkit`),
  )
  .aggregate(
    `phms-core`,
    `phms-db-config`,
    `phms-logger`,
    `phms-kernel`,
  )

lazy val `phms-email` = utilProject("email")
  .settings(
    libraryDependencies ++= Seq(
      Libraries.javaxMail,
      Libraries.ip4s,
    )
  )
  .dependsOn(
    `phms-core`,
    `phms-logger`,
    `phms-config`,
    `phms-kernel`,
    asTestingLibrary(`phms-testkit`),
  )
  .aggregate(
    `phms-core`,
    `phms-logger`,
    `phms-config`,
    `phms-kernel`,
  )

lazy val `phms-http` = utilProject("http")
  .settings(
    libraryDependencies ++= Seq(
      Libraries.http4sDSL,
      Libraries.http4sCirce,
      Libraries.http4sEmberServer,
    )
  )
  .dependsOn(
    `phms-core`,
    `phms-json`,
    asTestingLibrary(`phms-testkit`),
  )
  .aggregate(
    `phms-core`,
    `phms-json`,
  )

lazy val `phms-json` = utilProject("json")
  .settings(
    libraryDependencies ++= Seq(
      Libraries.circeCore,
      Libraries.circeGeneric,
    )
  )
  .dependsOn(
    `phms-core`,
    `phms-kernel`,
    `phms-time`,
  )
  .aggregate(
    `phms-core`,
    `phms-kernel`,
    `phms-time`,
  )

lazy val `phms-db-config` = utilProject("db-config")
  .settings(
    libraryDependencies ++= Seq(
      Libraries.flyway,
      Libraries.pgJDBC,
      Libraries.ip4s,
    )
  )
  .dependsOn(
    `phms-config`,
    `phms-core`,
  )
  .aggregate(
    `phms-config`,
    `phms-core`,
  )

lazy val `phms-config` = utilProject("config")
  .settings(
    libraryDependencies ++= Seq(
      Libraries.ciris,
      Libraries.ip4s,
    )
  )
  .dependsOn(
    `phms-core`
  )
  .aggregate(
    `phms-core`
  )

lazy val `phms-logger` = utilProject("logger")
  .settings(
    libraryDependencies ++= Seq(
      Libraries.log4cats,
      Libraries.logbackClassic,
    )
  )
  .dependsOn(
    `phms-core`
  )
  .aggregate(
    `phms-core`
  )

lazy val `phms-crypto` = utilProject("crypto")
  .settings(
    libraryDependencies ++= Seq(
      Libraries.javaBcrypt
    )
  )
  .dependsOn(
    `phms-core`,
    `phms-kernel`,
    asTestingLibrary(`phms-testkit`),
  )
  .aggregate(
    `phms-core`,
    `phms-kernel`,
  )

lazy val `phms-kernel` = utilProject("kernel")
  .settings(
    libraryDependencies ++= Seq()
  )
  .dependsOn(
    `phms-core`
  )
  .aggregate(
    `phms-core`
  )

lazy val `phms-testkit` = utilProject("testkit")
  .settings(
    libraryDependencies ++= Seq(
      Libraries.munit
    )
  )
  .dependsOn(
    `phms-core`
  )
  .aggregate(
    `phms-core`
  )

lazy val `phms-core` = utilProject("core")
  .settings(
    libraryDependencies ++= Seq(
      Libraries.sprout,
      Libraries.phCore,
      Libraries.cats,
      Libraries.catsEffect,
      Libraries.fs2Core,
    )
  )
  .dependsOn(
  )
  .aggregate(
  )

//=============================================================================
//=============================================================================

def genericProject(id: String, folder: String, name: String): Project =
  Project(s"$id-$name", file(s"$folder/$name"))
    .settings(commonSettings)

def algebraProject(name: String): Project = genericProject("algebra", "module/algebras", name)
def utilProject(name:    String): Project = genericProject("phms", "module/utils", name)
def troveProject(name:   String): Project = genericProject("phms", "module/troves", name)
def serviceProject(name: String): Project = genericProject("service", "module/services", name)
def restProject(name:    String): Project = genericProject("rest", "module/rest", name)

/** See SBT docs:
  * https://www.scala-sbt.org/release/docs/Multi-Project.html#Per-configuration+classpath+dependencies
  *
  * or an example:
  * {{{
  * val testModule = project
  *
  * val prodModule = project
  *   .dependsOn(asTestingLibrary(testModule))
  * }}}
  * To ensure that testing code and dependencies
  * do not wind up in the "compile" (i.e.) prod part of your
  * application.
  */
def asTestingLibrary(p: Project): ClasspathDependency = p % "test -> compile"
