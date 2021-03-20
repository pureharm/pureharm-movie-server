import sbt._
import Settings._

addCommandAlias("mkSite", ";docs/clean;docs/makeMicrosite")
addCommandAlias("doSitePublish", ";docs/clean;docs/publishMicrosite")
addCommandAlias("mkJar", ";clean;update;compile;server/assembly")

//=============================================================================
//=============================================================================
ThisBuild / version := "1.0.0"

lazy val server = Project(id = "server", file("./module/apps/pms"))
  .settings(commonSettings)
  .enablePlugins(JavaAppPackaging)
  .settings(
    Compile / mainClass := Option("pms.server.PureMovieServerApp")
  )
  .settings(
    libraryDependencies ++= Seq(
    )
  )
  .dependsOn(
    `pms-core`,
    `pms-logger`,
    `pms-config`,
    `pms-db-config`,
    `pms-http`,
    `pms-core`,
    `service-user`,
    `service-movie`,
    `rest-user`,
    `rest-movie`,
    `bootstrap`,
  )
  .aggregate(
    `pms-core`,
    `pms-logger`,
    `pms-config`,
    `pms-db-config`,
    `pms-http`,
    `pms-core`,
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
    `pms-logger`,
    `pms-core`,
    `pms-config`,
    `pms-core`,
    `pms-db-config`,
    `algebra-user`,
  )
  .aggregate(
    `pms-logger`,
    `pms-core`,
    `pms-config`,
    `pms-db-config`,
    `pms-core`,
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
    `pms-email`,
    `pms-config`,
    `pms-logger`,
    `pms-core`,
    `pms-core`,
    `pms-json`,
    `pms-http`,
  )
  .aggregate(
    `algebra-user`,
    `algebra-http-sec`,
    `pms-email`,
    `pms-config`,
    `pms-logger`,
    `pms-core`,
    `pms-core`,
    `pms-json`,
    `pms-http`,
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
    `pms-config`,
    `pms-logger`,
    `pms-core`,
    `pms-core`,
    `pms-json`,
    `pms-http`,
  )
  .aggregate(
    `algebra-user`,
    `algebra-imdb`,
    `algebra-movie`,
    `algebra-http-sec`,
    `pms-config`,
    `pms-logger`,
    `pms-core`,
    `pms-core`,
    `pms-json`,
    `pms-http`,
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
    `pms-email`,
    `pms-config`,
    `pms-logger`,
    `pms-core`,
    `pms-json`,
    `pms-http`,
  )
  .aggregate(
    `algebra-user`,
    `service-user`,
    `algebra-http-sec`,
    `pms-email`,
    `pms-config`,
    `pms-logger`,
    `pms-core`,
    `pms-json`,
    `pms-http`,
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
    `pms-config`,
    `pms-logger`,
    `pms-core`,
    `pms-json`,
    `pms-http`,
  )
  .aggregate(
    `algebra-user`,
    `service-movie`,
    `algebra-movie`,
    `algebra-http-sec`,
    `pms-config`,
    `pms-logger`,
    `pms-core`,
    `pms-json`,
    `pms-http`,
  )

lazy val `algebra-http-sec` = algebraProject("http-sec")
  .settings(
    libraryDependencies ++= Seq(
    )
  )
  .dependsOn(
    `pms-config`,
    `pms-core`,
    `pms-http`,
    `algebra-user`,
  )
  .aggregate(
    `pms-config`,
    `pms-core`,
    `pms-http`,
    `algebra-user`,
  )

lazy val `algebra-imdb` = algebraProject("imdb")
  .settings(
    libraryDependencies ++= Seq(
      Libraries.scalaScrapper
    )
  )
  .dependsOn(
    `pms-config`,
    `pms-logger`,
    `pms-core`,
  )
  .aggregate(
    `pms-config`,
    `pms-logger`,
    `pms-core`,
  )

lazy val `algebra-movie` = algebraProject("movie")
  .settings(
    libraryDependencies ++= Seq(
    )
  )
  .dependsOn(
    `algebra-user`,
    `pms-config`,
    `pms-core`,
    `pms-db`,
  )
  .aggregate(
    `algebra-user`,
    `pms-config`,
    `pms-core`,
    `pms-db`,
  )

lazy val `algebra-user` = algebraProject("user")
  .settings(
    libraryDependencies ++= Seq(
    )
  )
  .dependsOn(
    `pms-config`,
    `pms-core`,
    `pms-email`,
    `pms-db`,
    `pms-crypto`,
    `pms-kernel`,
  )
  .aggregate(
    `pms-config`,
    `pms-core`,
    `pms-email`,
    `pms-db`,
    `pms-crypto`,
    `pms-kernel`,
  )

lazy val `pms-db` = utilProject("db")
  .settings(
    libraryDependencies ++= Seq(
      Libraries.skunk
    )
  )
  .dependsOn(
    `pms-core`,
    `pms-db-config`,
    `pms-logger`,
    `pms-kernel`,
  )
  .aggregate(
    `pms-core`,
    `pms-db-config`,
    `pms-logger`,
    `pms-kernel`,
  )

lazy val `pms-email` = utilProject("email")
  .settings(
    libraryDependencies ++= Seq(
      Libraries.javaxMail,
      Libraries.ip4s,
    )
  )
  .dependsOn(
    `pms-core`,
    `pms-logger`,
    `pms-config`,
    `pms-kernel`,
  )
  .aggregate(
    `pms-core`,
    `pms-logger`,
    `pms-config`,
    `pms-kernel`,
  )

lazy val `pms-http` = utilProject("http")
  .settings(
    libraryDependencies ++= Seq(
      Libraries.http4sDSL,
      Libraries.http4sCirce,
      Libraries.http4sEmberServer,
    )
  )
  .dependsOn(
    `pms-core`,
    `pms-json`,
  )
  .aggregate(
    `pms-core`,
    `pms-json`,
  )

lazy val `pms-json` = utilProject("json")
  .settings(
    libraryDependencies ++= Seq(
      Libraries.circeCore,
      Libraries.circeGeneric,
    )
  )
  .dependsOn(
    `pms-core`,
    `pms-kernel`,
  )
  .aggregate(
    `pms-core`,
    `pms-kernel`,
  )

lazy val `pms-db-config` = utilProject("db-config")
  .settings(
    libraryDependencies ++= Seq(
      Libraries.flyway,
      Libraries.ip4s,
    )
  )
  .dependsOn(
    `pms-config`,
    `pms-core`,
  )
  .aggregate(
    `pms-config`,
    `pms-core`,
  )

lazy val `pms-config` = utilProject("config")
  .settings(
    libraryDependencies ++= Seq(
      Libraries.ciris,
      Libraries.ip4s,
    )
  )
  .dependsOn(
    `pms-core`
  )
  .aggregate(
    `pms-core`
  )

lazy val `pms-logger` = utilProject("logger")
  .settings(
    libraryDependencies ++= Seq(
      Libraries.log4cats,
      Libraries.logbackClassic,
    )
  )
  .dependsOn(
    `pms-core`
  )
  .aggregate(
    `pms-core`
  )

lazy val `pms-crypto` = utilProject("crypto")
  .settings(
    libraryDependencies ++= Seq(
      Libraries.javaBcrypt
    )
  )
  .dependsOn(
    `pms-core`,
    `pms-kernel`,
  )
  .aggregate(
    `pms-core`,
    `pms-kernel`,
  )

lazy val `pms-kernel` = utilProject("kernel")
  .settings(
    libraryDependencies ++= Seq()
  )
  .dependsOn(
    `pms-core`
  )
  .aggregate(
    `pms-core`
  )

lazy val `pms-core` = utilProject("core")
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
def utilProject(name:    String): Project = genericProject("pms", "module/utils", name)
def serviceProject(name: String): Project = genericProject("service", "module/services", name)
def restProject(name:    String): Project = genericProject("rest", "module/rest", name)
