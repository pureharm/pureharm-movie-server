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
      Libraries.specs2 % Test
    )
  )
  .dependsOn(
    `pms-effects`,
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
    `pms-effects`,
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
      Libraries.specs2 % Test
    )
  )
  .dependsOn(
    `pms-logger`,
    `pms-effects`,
    `pms-config`,
    `pms-core`,
    `pms-db-config`,
    `algebra-user`,
  )
  .aggregate(
    `pms-logger`,
    `pms-effects`,
    `pms-config`,
    `pms-db-config`,
    `pms-core`,
    `algebra-user`,
  )

lazy val `service-user` = serviceProject("user")
  .settings(
    libraryDependencies ++= Seq(
      Libraries.specs2 % Test
    )
  )
  .dependsOn(
    `algebra-user`,
    `algebra-http-sec`,
    `pms-email`,
    `pms-config`,
    `pms-logger`,
    `pms-effects`,
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
    `pms-effects`,
    `pms-core`,
    `pms-json`,
    `pms-http`,
  )

lazy val `service-movie` = serviceProject("movie")
  .settings(
    libraryDependencies ++= Seq(
      Libraries.spire,
      Libraries.specs2 % Test,
    )
  )
  .dependsOn(
    `algebra-user`,
    `algebra-imdb`,
    `algebra-movie`,
    `algebra-http-sec`,
    `pms-config`,
    `pms-logger`,
    `pms-effects`,
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
    `pms-effects`,
    `pms-core`,
    `pms-json`,
    `pms-http`,
  )

lazy val `rest-user` = restProject("user")
  .settings(
    libraryDependencies ++= Seq(
      Libraries.specs2 % Test
    )
  )
  .dependsOn(
    `algebra-user`,
    `service-user`,
    `algebra-http-sec`,
    `pms-email`,
    `pms-config`,
    `pms-logger`,
    `pms-effects`,
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
    `pms-effects`,
    `pms-core`,
    `pms-json`,
    `pms-http`,
  )

lazy val `rest-movie` = restProject("movie")
  .settings(
    libraryDependencies ++= Seq(
      Libraries.spire,
      Libraries.specs2 % Test,
    )
  )
  .dependsOn(
    `algebra-user`,
    `service-movie`,
    `algebra-movie`,
    `algebra-http-sec`,
    `pms-config`,
    `pms-logger`,
    `pms-effects`,
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
    `pms-effects`,
    `pms-core`,
    `pms-json`,
    `pms-http`,
  )

lazy val `algebra-http-sec` = algebraProject("http-sec")
  .settings(
    libraryDependencies ++= Seq(
      Libraries.specs2 % Test
    )
  )
  .dependsOn(
    `pms-config`,
    `pms-effects`,
    `pms-core`,
    `pms-http`,
    `algebra-user`,
  )
  .aggregate(
    `pms-config`,
    `pms-effects`,
    `pms-core`,
    `pms-http`,
    `algebra-user`,
  )

lazy val `algebra-imdb` = algebraProject("imdb")
  .settings(
    libraryDependencies ++= Seq(
      Libraries.scalaScrapper,
      Libraries.specs2 % Test,
    )
  )
  .dependsOn(
    `pms-config`,
    `pms-logger`,
    `pms-effects`,
    `pms-core`,
  )
  .aggregate(
    `pms-config`,
    `pms-logger`,
    `pms-effects`,
    `pms-core`,
  )

lazy val `algebra-movie` = algebraProject("movie")
  .settings(
    libraryDependencies ++= Seq(
      Libraries.spire,
      Libraries.specs2 % Test,
    )
  )
  .dependsOn(
    `algebra-user`,
    `pms-config`,
    `pms-effects`,
    `pms-core`,
    `pms-db`,
  )
  .aggregate(
    `algebra-user`,
    `pms-config`,
    `pms-effects`,
    `pms-core`,
    `pms-db`,
  )

lazy val `algebra-user` = algebraProject("user")
  .settings(
    libraryDependencies ++= Seq(
      Libraries.specs2 % Test
    ) ++ Libraries.tsec
  )
  .dependsOn(
    `pms-config`,
    `pms-effects`,
    `pms-core`,
    `pms-email`,
    `pms-db`,
  )
  .aggregate(
    `pms-config`,
    `pms-effects`,
    `pms-core`,
    `pms-email`,
    `pms-db`,
  )

lazy val `pms-db` = utilProject("db")
  .settings(
    libraryDependencies ++= Seq(
      Libraries.phFlyway,
      Libraries.specs2 % Test,
    ) ++ Libraries.doobie ++ Libraries.fs2
  )
  .dependsOn(
    `pms-effects`,
    `pms-db-config`,
    `pms-logger`,
    `pms-core`,
  )
  .aggregate(
    `pms-effects`,
    `pms-db-config`,
    `pms-logger`,
    `pms-core`,
  )

lazy val `pms-email` = utilProject("email")
  .settings(
    libraryDependencies ++= Seq(
      Libraries.javaxMail,
      Libraries.specs2 % Test,
    )
  )
  .dependsOn(
    `pms-core`,
    `pms-logger`,
    `pms-effects`,
    `pms-config`,
  )
  .aggregate(
    `pms-core`,
    `pms-logger`,
    `pms-effects`,
    `pms-config`,
  )

lazy val `pms-http` = utilProject("http")
  .settings(
    libraryDependencies ++= Seq(
      Libraries.specs2 % Test
    ) ++ Libraries.http4s ++ Libraries.fs2
  )
  .dependsOn(
    `pms-core`,
    `pms-effects`,
    `pms-json`,
  )
  .aggregate(
    `pms-core`,
    `pms-effects`,
    `pms-json`,
  )

lazy val `pms-json` = utilProject("json")
  .settings(
    libraryDependencies ++= Seq(
      Libraries.phJson
    ) ++ Libraries.circe
  )
  .dependsOn(
    `pms-core`,
    `pms-effects`,
  )
  .aggregate(
    `pms-core`,
    `pms-effects`,
  )

lazy val `pms-db-config` = utilProject("db-config")
  .settings(
    libraryDependencies ++= Seq(
      Libraries.doobieCore,
      Libraries.doobieHikari,
      Libraries.phFlyway,
      Libraries.flyway,
    ) ++ Libraries.fs2
  )
  .dependsOn(
    `pms-config`,
    `pms-effects`,
  )
  .aggregate(
    `pms-config`,
    `pms-effects`,
  )

lazy val `pms-config` = utilProject("config")
  .settings(
    libraryDependencies ++= Seq(
      Libraries.pureConfig,
      Libraries.phConfig,
    )
  )
  .dependsOn(
    `pms-effects`
  )
  .aggregate(
    `pms-effects`
  )

lazy val `pms-logger` = utilProject("logger")
  .settings(
    libraryDependencies ++= Seq(
      Libraries.log4cats,
      Libraries.logbackClassic,
    )
  )
  .dependsOn(
    `pms-effects`
  )
  .aggregate(
    `pms-effects`
  )

lazy val `pms-core` = utilProject("core")
  .settings(
    libraryDependencies ++= Seq(
      Libraries.shapeless,
      Libraries.phCore,
      Libraries.specs2 % Test,
    )
  )
  .dependsOn(
    `pms-effects`
  )
  .aggregate(
    `pms-effects`
  )

lazy val `pms-effects` = utilProject("effects")
  .settings(
    libraryDependencies ++= Seq(
      Libraries.cats,
      Libraries.catsEffect,
      Libraries.phEffects,
      Libraries.specs2 % Test,
    )
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
