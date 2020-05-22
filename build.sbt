import sbt._

addCommandAlias("mkSite", ";docs/clean;docs/makeMicrosite")
addCommandAlias("doSitePublish", ";docs/clean;docs/publishMicrosite")
addCommandAlias("mkJar", ";clean;update;compile;server/assembly")

//=============================================================================
//=============================================================================

lazy val root =
  Project(id = "pure-movie-server", base = file("."))
    .settings(commonSettings)
    .aggregate(
      server
    )

lazy val server = project
  .settings(commonSettings)
  .settings(sbtAssemblySettings)
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
    `server-bootstrap`,
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
    `server-bootstrap`,
  )

lazy val `server-bootstrap` = project
  .settings(commonSettings)
  .settings(sbtAssemblySettings)
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
    .settings(sbtAssemblySettings)

def algebraProject(name: String): Project = genericProject("algebra", "algebras", name)
def utilProject(name:    String): Project = genericProject("pms", "pms-utils", name)
def serviceProject(name: String): Project = genericProject("service", "services", name)

def commonSettings: Seq[Setting[_]] = Seq(
  //https://github.com/scala/scala/releases
  scalaVersion                       := "2.13.2",
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
  addCompilerPlugin(("org.typelevel" %% "kind-projector" % "0.11.0").cross(CrossVersion.full)),
  scalacOptions ++= scala2_13Flags ++ betterForPluginCompilerFlags,
)

def sbtAssemblySettings: Seq[Setting[_]] = {
  import sbtassembly.MergeStrategy
  import sbtassembly.PathList

  baseAssemblySettings ++
    Seq(
      // Skip tests during while running the assembly task
      test in assembly                  := {},
      assemblyMergeStrategy in assembly := {
        case PathList("application.conf", _ @_*) => MergeStrategy.concat
        case "application.conf"                  => MergeStrategy.concat
        case PathList("reference.conf", _ @_*)   => MergeStrategy.concat
        case "reference.conf"                    => MergeStrategy.concat
        case x                                   => (assemblyMergeStrategy in assembly).value(x)
      },
      //this is to avoid propagation of the assembly task to all subprojects.
      //changing this makes assembly incredibly slow
      aggregate in assembly             := false,
    )
}

/**
  * tpolecat's glorious compile flag list adapted for scala 2.13 (fewer flags):
  * https://tpolecat.github.io/2017/04/25/scalac-flags.html
  */
def scala2_13Flags: Seq[String] = Seq(
  //"-Xfatal-warnings",            // Fail the compilation if there are any warnings.
  "-deprecation",                  // Emit warning and location for usages of deprecated APIs.
  "-encoding",                     // yeah, it's part of the "utf-8" thing, two flags
  "utf-8",                         // Specify character encoding used by source files.
  "-explaintypes",                 // Explain type errors in more detail.
  "-feature",                      // Emit warning and location for usages of features that should be imported explicitly.
  "-language:existentials",        // Existential types (besides wildcard types) can be written and inferred
  "-language:higherKinds",         // Allow higher-kinded types
  "-language:implicitConversions", // Allow definition of implicit functions called views
  "-unchecked",                    // Enable additional warnings where generated code depends on assumptions.
  "-Xcheckinit",                   // Wrap field accessors to throw an exception on uninitialized access.
  "-Xlint:adapted-args",           // Warn if an argument list is modified to match the receiver.
  "-Xlint:constant",               // Evaluation of a constant arithmetic expression results in an error.
  "-Xlint:delayedinit-select",     // Selecting member of DelayedInit.
  "-Xlint:doc-detached",           // A Scaladoc comment appears to be detached from its element.
  "-Xlint:inaccessible",           // Warn about inaccessible types in method signatures.
  "-Xlint:infer-any",              // Warn when a type argument is inferred to be `Any`.
  "-Xlint:missing-interpolator",   // A string literal appears to be missing an interpolator id.
  "-Xlint:nullary-override",       // Warn when non-nullary `def f()' overrides nullary `def f'.
  "-Xlint:nullary-unit",           // Warn when nullary methods return Unit.
  "-Xlint:option-implicit",        // Option.apply used implicit view.
  "-Xlint:package-object-classes", // Class or object defined in package object.
  "-Xlint:poly-implicit-overload", // Parameterized overloaded implicit methods are not visible as view bounds.
  "-Xlint:private-shadow",         // A private field (or class parameter) shadows a superclass field.
  "-Xlint:stars-align",            // Pattern sequence wildcard must align with sequence component.
  "-Xlint:type-parameter-shadow",  // A local type parameter shadows a type already in scope.
  "-Ywarn-extra-implicit",         // Warn when more than one implicit parameter section is defined.
  "-Ywarn-numeric-widen",          // Warn when numerics are widened.
  "-Ywarn-unused:implicits",       // Warn if an implicit parameter is unused.
  "-Ywarn-unused:imports",         // Warn if an import selector is not referenced.
  "-Ywarn-unused:locals",          // Warn if a local definition is unused.
  "-Ywarn-unused:params",          // Warn if a value parameter is unused.
  "-Ywarn-unused:patvars",         // Warn if a variable bound in a pattern is unused.
  "-Ywarn-unused:privates",        // Warn if a private member is unused.
  "-Ywarn-value-discard",          // Warn when non-Unit expression results are unused.
)

/**
  * These are flags specific to the "better-monadic-for" plugin:
  * https://github.com/oleg-py/better-monadic-for
  */
def betterForPluginCompilerFlags: Seq[String] = Seq(
  "-P:bm4:no-filtering:y",     // see https://github.com/oleg-py/better-monadic-for#desugaring-for-patterns-without-withfilters--pbm4no-filteringy
  "-P:bm4:no-map-id:y",        // see https://github.com/oleg-py/better-monadic-for#final-map-optimization--pbm4no-map-idy
  "-P:bm4:no-tupling:y",       // see https://github.com/oleg-py/better-monadic-for#desugar-bindings-as-vals-instead-of-tuples--pbm4no-tuplingy
  "-P:bm4:implicit-patterns:y",//see https://github.com/oleg-py/better-monadic-for#define-implicits-in-for-comprehensions-or-matches
)

//============================================================================================
//============================================================================================
//======================================= DEPENDENCIES =======================================
//============================================================================================
//============================================================================================

lazy val pureHarmVersion       = "0.0.5"     //https://github.com/busymachines/pureharm/releases
lazy val catsCoreVersion       = "2.1.1"     //https://github.com/typelevel/cats/releases
lazy val catsEffectVersion     = "2.1.3"     //https://github.com/typelevel/cats-effect/releases
lazy val fs2Version            = "2.3.0"     //https://github.com/functional-streams-for-scala/fs2/releases
lazy val circeVersion          = "0.13.0"    //https://github.com/circe/circe/releases
lazy val http4sVersion         = "0.21.4"    //https://github.com/http4s/http4s/releases
lazy val tsecVersion           = "0.2.0"     //https://github.com/jmcardon/tsec/releases
lazy val doobieVersion         = "0.9.0"     //https://github.com/tpolecat/doobie/releases
lazy val flywayVersion         = "6.4.1"     //https://github.com/flyway/flyway/releases
lazy val shapelessVersion      = "2.3.3"     //https://github.com/milessabin/shapeless/releases
lazy val spireVersion          = "0.17.0-M1" //https://github.com/non/spire/releases
lazy val log4catsVersion       = "1.1.1"     //https://github.com/ChristopherDavenport/log4cats/releases
lazy val logbackClassicVersion = "1.2.3"     //https://github.com/qos-ch/logback/releases
lazy val javaxMailVersion      = "1.6.2"     // https://github.com/javaee/javamail/releases
lazy val scalaScrapperVersion  = "2.2.0"     //https://github.com/ruippeixotog/scala-scraper/releases
lazy val pureConfigVersion     = "0.12.3"    //https://github.com/pureconfig/pureconfig/releases
lazy val specs2Version         = "4.9.4"     //https://github.com/etorreborre/specs2/releases

def pureharm(m: String): ModuleID = "com.busymachines" %% s"pureharm-$m" % pureHarmVersion withSources ()

lazy val phCore:    ModuleID = pureharm("core")
lazy val phConfig:  ModuleID = pureharm("config")
lazy val phJson:    ModuleID = pureharm("json-circe")
lazy val phFlyway:  ModuleID = pureharm("db-core-flyway")
lazy val phEffects: ModuleID = pureharm("effects-cats")

//============================================================================================
//================================= http://typelevel.org/scala/ ==============================
//========================================  typelevel ========================================
//============================================================================================

lazy val catsCore:   ModuleID = "org.typelevel" %% "cats-core"   % catsCoreVersion withSources ()
lazy val catsKernel: ModuleID = "org.typelevel" %% "cats-kernel" % catsCoreVersion withSources ()
lazy val catsMacros: ModuleID = "org.typelevel" %% "cats-macros" % catsCoreVersion withSources ()
lazy val catsFree:   ModuleID = "org.typelevel" %% "cats-free"   % catsCoreVersion withSources ()

lazy val cats:       Seq[ModuleID] = Seq(catsCore, catsKernel, catsMacros, catsFree)
lazy val catsEffect: ModuleID      = "org.typelevel" %% "cats-effect" % catsEffectVersion withSources ()

lazy val fs2Core: ModuleID = "co.fs2" %% "fs2-core" % fs2Version withSources ()
lazy val fs2IO:   ModuleID = "co.fs2" %% "fs2-io"   % fs2Version withSources ()

lazy val fs2: Seq[ModuleID] = Seq(fs2Core, fs2IO)

lazy val circeCore:          ModuleID = "io.circe" %% "circe-core"           % circeVersion
lazy val circeGeneric:       ModuleID = "io.circe" %% "circe-generic"        % circeVersion
lazy val circeGenericExtras: ModuleID = "io.circe" %% "circe-generic-extras" % circeVersion

lazy val circe: Seq[ModuleID] = Seq(circeCore, circeGeneric, circeGenericExtras)

lazy val http4sBlazeServer: ModuleID = "org.http4s" %% "http4s-blaze-server" % http4sVersion withSources ()
lazy val http4sCirce:       ModuleID = "org.http4s" %% "http4s-circe"        % http4sVersion withSources ()
lazy val http4sDSL:         ModuleID = "org.http4s" %% "http4s-dsl"          % http4sVersion withSources ()

lazy val http4s: Seq[ModuleID] = Seq(http4sBlazeServer, http4sCirce, http4sDSL)

lazy val doobieCore     = "org.tpolecat" %% "doobie-core"     % doobieVersion withSources ()
lazy val doobieHikari   = "org.tpolecat" %% "doobie-hikari"   % doobieVersion withSources ()
lazy val doobiePostgres = "org.tpolecat" %% "doobie-postgres" % doobieVersion withSources ()
lazy val doobieTK       = "org.tpolecat" %% "doobie-specs2"   % doobieVersion           % Test withSources ()

lazy val doobie: Seq[ModuleID] = Seq(doobieCore, doobieHikari, doobiePostgres, doobieTK)

lazy val shapeless: ModuleID = "com.chuusai" %% "shapeless" % shapelessVersion withSources ()

lazy val flyway = "org.flywaydb" % "flyway-core" % flywayVersion withSources ()

//============================================================================================
//==========================================  math ===========================================
//============================================================================================

lazy val spire: ModuleID = "org.typelevel" %% "spire" % spireVersion withSources ()

//============================================================================================
//========================================  security  ========================================
//============================================================================================

lazy val tsec: Seq[ModuleID] = Seq(
  "io.github.jmcardon" %% "tsec-common"   % tsecVersion withSources (),
  "io.github.jmcardon" %% "tsec-password" % tsecVersion withSources (),
  "io.github.jmcardon" %% "tsec-mac"      % tsecVersion withSources (),
  "io.github.jmcardon" %% "tsec-jwt-mac"  % tsecVersion withSources (),
)

//============================================================================================
//=========================================  logging =========================================
//============================================================================================

lazy val log4cats = "io.chrisdavenport" %% "log4cats-slf4j" % log4catsVersion withSources ()

//this is a Java library, notice that we used one single % instead of %%
//it is the backend implementation used by log4cats
lazy val logbackClassic = "ch.qos.logback" % "logback-classic" % logbackClassicVersion withSources ()

//============================================================================================
//==========================================  email ==========================================
//============================================================================================

//this is a Java library, notice that we used one single % instead of %%
lazy val javaxMail = "com.sun.mail" % "javax.mail" % javaxMailVersion withSources ()

//============================================================================================
//========================================= html =============================================
//============================================================================================

lazy val scalaScrapper = "net.ruippeixotog" %% "scala-scraper" % scalaScrapperVersion withSources ()

//============================================================================================
//========================================== config ==========================================
//============================================================================================

lazy val pureConfig: ModuleID = "com.github.pureconfig" %% "pureconfig" % pureConfigVersion withSources ()

//============================================================================================
//=========================================  testing =========================================
//============================================================================================

lazy val specs2: ModuleID = "org.specs2" %% "specs2-core" % specs2Version withSources ()

lazy val specs2Test: ModuleID = specs2 % Test

//============================================================================================
//=======================================  transitive ========================================
//============================================================================================
//these are transitive dependencies that are brought in by other libraries, and here we
//list the ones that tend to come with conflicting version so that we can override them
//so as to remove the annoying eviction warning of older version. This list will have to
//be curated with great care from time to time.
lazy val transitive = Seq(
  //---------------------------
  //https://commons.apache.org/proper/commons-codec/
  //tsec, and http4s depend on this
  "commons-codec"  % "commons-codec" % "1.12"  withSources (),
  //---------------------------
  //https://github.com/Log4s/log4s
  //different http4s modules depend on different versions
  "org.log4s"     %% "log4s"         % "1.7.0" withSources (),
  //---------------------------
  //https://github.com/typelevel/machinist
  //spire and cats core depend on this
  "org.typelevel" %% "machinist"     % "0.6.6" withSources (),
)
