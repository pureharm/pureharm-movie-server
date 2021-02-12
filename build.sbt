import sbt._
import Dependencies._
import Settings._

addCommandAlias("mkSite", ";docs/clean;docs/makeMicrosite")
addCommandAlias("doSitePublish", ";docs/clean;docs/publishMicrosite")
addCommandAlias("mkJar", ";clean;update;compile;server/assembly")

//=============================================================================
//=============================================================================

lazy val server = Project(id = "server", file("./module/apps/pms"))
  .settings(commonSettings)
  .settings(AssemblySettings.settings)
  .settings(
    mainClass                   := Option("pms.server.PureMovieServerApp"),
    mainClass in assembly       := Option("pms.server.PureMovieServerApp"),
    assemblyJarName in assembly := s"pure-movie-server.jar",
  )
  .settings(
    libraryDependencies ++= Seq(
      specs2Test
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
    `bootstrap`,
  )

lazy val `bootstrap` = project
  .settings(commonSettings)
  .settings(AssemblySettings.settings)
  .settings(
    libraryDependencies ++= Seq(
      specs2Test
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
      specs2Test
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
      spire,
      specs2Test,
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

lazy val `algebra-http-sec` = algebraProject("http-sec")
  .settings(
    libraryDependencies ++= Seq(
      specs2Test
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
      scalaScrapper,
      specs2Test,
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
      spire,
      specs2Test,
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
      specs2Test
    ) ++ tsec
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
      specs2Test
    ) ++ doobie ++ fs2
  )
  .dependsOn(
    `pms-effects`
  )
  .aggregate(
    `pms-effects`
  )

lazy val `pms-email` = utilProject("email")
  .settings(
    libraryDependencies ++= Seq(
      javaxMail,
      specs2Test,
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
      specs2Test
    ) ++ http4s ++ fs2
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
      phJson
    ) ++ circe
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
      doobieCore,
      doobieHikari,
      phFlyway,
      flyway,
    ) ++ fs2
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
      pureConfig,
      phConfig,
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
      log4cats,
      logbackClassic,
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
      shapeless,
      phCore,
      specs2Test,
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
    libraryDependencies ++= cats ++ Seq(
      catsEffect,
      phEffects,
      specs2Test,
    )
  )

//lazy val docs = project
//  .enablePlugins(MicrositesPlugin)
//  .enablePlugins(TutPlugin)
//  .disablePlugins(ScalafmtPlugin)
//  //.disablePlugins(ScalafixPlugin)
//  .settings(commonSettings)
//  .settings(micrositeTasksSettings)
//  .settings(
//    micrositeName              := "pure-movie-server",
//    micrositeDescription       := "Example web server written in a pure functional programming style",
//    micrositeBaseUrl           := "/pure-movie-server",
//    micrositeDocumentationUrl  := "/pure-movie-server/docs/",
//    micrositeHomepage          := "http://busymachines.github.io/pure-movie-server/",
//    micrositeGithubOwner       := "busymachines",
//    micrositeGithubRepo        := "pure-movie-server",
//    micrositeHighlightTheme    := "atom-one-light",
//    //-------------- docs project ------------
//    //micrositeImgDirectory := (resourceDirectory in Compile).value / "microsite" / "images",
//    //micrositeCssDirectory := (resourceDirectory in Compile).value / "microsite" / "styles"
//    //micrositeJsDirectory := (resourceDirectory in Compile).value / "microsite" / "scripts"
//    micrositePalette           := Map(
//      "brand-primary"   -> "#E05236",
//      "brand-secondary" -> "#3F3242",
//      "brand-tertiary"  -> "#2D232F",
//      "gray-dark"       -> "#453E46",
//      "gray"            -> "#837F84",
//      "gray-light"      -> "#E3E2E3",
//      "gray-lighter"    -> "#F4F3F4",
//      "white-color"     -> "#FFFFFF",
//    ),
//    //micrositeFavicons := Seq(
//    //  MicrositeFavicon("favicon16x16.png", "16x16"),
//    //  MicrositeFavicon("favicon32x32.png", "32x32")
//    //),
//    micrositeFooterText        := Some("""Ⓒ 2020 <a href="https://www.busymachines.com/">BusyMachines</a>"""),
//    //------ same as default settings --------
//    micrositePushSiteWith      := GHPagesPlugin,
//    micrositeGitHostingService := GitHub,
//  )
//  .dependsOn()

//=============================================================================
//=============================================================================

def genericProject(id: String, folder: String, name: String): Project =
  Project(s"$id-$name", file(s"$folder/$name"))
    .settings(commonSettings)
    .settings(AssemblySettings.settings)

def algebraProject(name: String): Project = genericProject("algebra", "module/algebras", name)
def utilProject(name:    String): Project = genericProject("pms", "module/utils", name)
def serviceProject(name: String): Project = genericProject("service", "module/services", name)
