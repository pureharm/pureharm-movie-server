import sbt._

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
    mainClass := Option("pms.server.PureMovieServerApp")
  )
  .dependsOn(
    `pms-effects`,
    `pms-config`,
    `pms-db-config`,
    `pms-core`,
    `service-user`,
    `service-movie`,
    `server-bootstrap`,
  )
  .aggregate(
    `pms-effects`,
    `pms-config`,
    `pms-db-config`,
    `pms-core`,
    `service-user`,
    `service-movie`,
    `server-bootstrap`,
  )

lazy val `server-bootstrap` = project
  .settings(commonSettings)
  .settings(sbtAssemblySettings)
  .settings()
  .dependsOn(
    `pms-effects`,
    `pms-config`,
    `pms-db-config`,
    `pms-core`,
    `algebra-user`,
  )
  .aggregate(
    `pms-effects`,
    `pms-config`,
    `pms-db-config`,
    `pms-core`,
    `algebra-user`,
  )

lazy val `service-user` = project
  .settings(commonSettings)
  .settings(sbtAssemblySettings)
  .dependsOn(
    `algebra-user`,
    `algebra-http-sec`,
    `pms-email`,
    `pms-config`,
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
    `pms-effects`,
    `pms-core`,
    `pms-json`,
    `pms-http`,
  )

lazy val `service-movie` = project
  .settings(commonSettings)
  .settings(sbtAssemblySettings)
  .dependsOn(
    `algebra-user`,
    `algreba-imdb`,
    `algebra-movie`,
    `algebra-http-sec`,
    `pms-config`,
    `pms-effects`,
    `pms-core`,
    `pms-json`,
    `pms-http`,
  )
  .aggregate(
    `algebra-user`,
    `algreba-imdb`,
    `algebra-movie`,
    `algebra-http-sec`,
    `pms-config`,
    `pms-effects`,
    `pms-core`,
    `pms-json`,
    `pms-http`,
  )

lazy val `algebra-http-sec` = project
  .settings(commonSettings)
  .settings(sbtAssemblySettings)
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

lazy val `algreba-imdb` = project
  .settings(commonSettings)
  .settings(sbtAssemblySettings)
  .dependsOn(
    `pms-config`,
    `pms-effects`,
    `pms-core`,
  )
  .aggregate(
    `pms-config`,
    `pms-effects`,
    `pms-core`,
  )

lazy val `algebra-movie` = project
  .settings(commonSettings)
  .settings(sbtAssemblySettings)
  .dependsOn(
    `algebra-user`,
    `pms-config`,
    `pms-effects`,
    `pms-core`,
  )
  .aggregate(
    `algebra-user`,
    `pms-config`,
    `pms-effects`,
    `pms-core`,
  )

lazy val `algebra-user` = project
  .settings(commonSettings)
  .settings(sbtAssemblySettings)
  .dependsOn(
    `pms-config`,
    `pms-effects`,
    `pms-core`,
  )
  .aggregate(
    `pms-config`,
    `pms-effects`,
    `pms-core`,
  )

lazy val `pms-email` = project
  .settings(commonSettings)
  .settings(sbtAssemblySettings)
  .dependsOn(
    `pms-core`,
    `pms-effects`,
    `pms-config`,
  )

lazy val `pms-http` = project
  .settings(commonSettings)
  .settings(sbtAssemblySettings)
  .dependsOn(
    `pms-core`,
    `pms-effects`,
    `pms-json`,
  )

lazy val `pms-json` = project
  .settings(commonSettings)
  .settings(sbtAssemblySettings)
  .dependsOn(
    `pms-core`,
    `pms-effects`,
  )

lazy val `pms-core` = project
  .settings(commonSettings)
  .settings(sbtAssemblySettings)
  .settings(
    libraryDependencies ++= Seq(
      shapeless
    )
  )
  .dependsOn(
    `pms-effects`
  )

lazy val `pms-config` = project
  .settings(commonSettings)
  .settings(sbtAssemblySettings)
  .dependsOn(
    `pms-effects`
  )

lazy val `pms-db-config` = project
  .settings(commonSettings)
  .settings(sbtAssemblySettings)
  .dependsOn(
    `pms-config`,
    `pms-effects`
  )

lazy val `pms-effects` = project
  .settings(commonSettings)
  .settings(sbtAssemblySettings)

//=============================================================================
//=============================================================================

def commonSettings: Seq[Setting[_]] = Seq(
  scalaVersion := "2.12.6",
  libraryDependencies ++= Seq(
    //utils
    bmcCore,
    bmcDuration,
    //effects + streams
    catsCore,
    catsEffect,
    monix,
    fs2,
    bmcEffects,
    //JSON stuff
    circeCore,
    circeGeneric,
    circeGenericExtras,
    bmcJson,
    //http4s
    http4sBlazeServer,
    http4sCirce,
    //doobie
    doobieHikari,
    doobiePostgres,
    //logging
    log4cats,
    logbackClassic,
    //email
    javaxMail,
    //test stuff
    doobieTK,
    //misc
    flyway,
    attoParser,
    pureConfig,
    spire,
    scalaScrapper,
  ) ++ tsec,
  /*
   * Eliminates useless, unintuitive, and sometimes broken additions of `withFilter`
   * when using generator arrows in for comprehensions. e.g.
   *
   * Vanila scala:
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
  addCompilerPlugin("com.olegpy" %% "better-monadic-for" % "0.2.4"),
  scalacOptions ++= customScalaCompileFlags,
  /**
    * This is here to eliminate eviction warnings from SBT.
    * The eco-system is mid-upgrade, so not all dependencies
    * depend on this newest cats, and cats-effect.
    *
    * See more on binary compatability:
    * https://docs.oracle.com/javase/specs/jls/se7/html/jls-13.html
    *
    * It is an important issue that you need to keep track of if
    * you build apps on the JVM.
    */
  dependencyOverrides += "org.typelevel" %% "cats-core"   % "1.1.0",
  dependencyOverrides += "org.typelevel" %% "cats-effect" % "0.10.1",
  resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"
)

def sbtAssemblySettings: Seq[Setting[_]] = {
  import sbtassembly.MergeStrategy
  import sbtassembly.PathList

  baseAssemblySettings ++
    Seq(
      // Skip tests during while running the assembly task
      test in assembly := {},
      assemblyMergeStrategy in assembly := {
        case PathList("application.conf", _ @_*) => MergeStrategy.concat
        case "application.conf" => MergeStrategy.concat
        case PathList("reference.conf", _ @_*) => MergeStrategy.concat
        case "reference.conf" => MergeStrategy.concat
        case x                => (assemblyMergeStrategy in assembly).value(x)
      },
      //this is to avoid propagation of the assembly task to all subprojects.
      //changing this makes assembly incredibly slow
      aggregate in assembly := false
    )
}

/**
  * tpolecat's glorious compile flag list:
  * https://tpolecat.github.io/2017/04/25/scalac-flags.html
  */
def customScalaCompileFlags: Seq[String] = Seq(
  "-deprecation", // Emit warning and location for usages of deprecated APIs.
  "-encoding",
  "utf-8", // Specify character encoding used by source files.
  "-Yrangepos",
  "-explaintypes", // Explain type errors in more detail.
  "-feature", // Emit warning and location for usages of features that should be imported explicitly.
  "-language:existentials", // Existential types (besides wildcard types) can be written and inferred
  "-language:higherKinds", // Allow higher-kinded types
  "-language:implicitConversions", // Allow definition of implicit functions called views
  "-unchecked", // Enable additional warnings where generated code depends on assumptions.
  "-Xcheckinit", // Wrap field accessors to throw an exception on uninitialized access.
  "-Xfuture", // Turn on future language features.
  "-Xlint:adapted-args", // Warn if an argument list is modified to match the receiver.
  "-Xlint:by-name-right-associative", // By-name parameter of right associative operator.
  "-Xlint:constant", // Evaluation of a constant arithmetic expression results in an error.
  "-Xlint:delayedinit-select", // Selecting member of DelayedInit.
  "-Xlint:doc-detached", // A Scaladoc comment appears to be detached from its element.
  "-Xlint:inaccessible", // Warn about inaccessible types in method signatures.
  "-Xlint:infer-any", // Warn when a type argument is inferred to be `Any`.
  "-Xlint:missing-interpolator", // A string literal appears to be missing an interpolator id.
  "-Xlint:nullary-override", // Warn when non-nullary `def f()' overrides nullary `def f'.
  "-Xlint:nullary-unit", // Warn when nullary methods return Unit.
  "-Xlint:option-implicit", // Option.apply used implicit view.
  "-Xlint:package-object-classes", // Class or object defined in package object.
  "-Xlint:poly-implicit-overload", // Parameterized overloaded implicit methods are not visible as view bounds.
  "-Xlint:private-shadow", // A private field (or class parameter) shadows a superclass field.
  "-Xlint:stars-align", // Pattern sequence wildcard must align with sequence component.
  "-Xlint:type-parameter-shadow", // A local type parameter shadows a type already in scope.
  "-Xlint:unsound-match", // Pattern match may not be typesafe.
  "-Yno-adapted-args", // Do not adapt an argument list (either by inserting () or creating a tuple) to match the receiver.
  "-Ywarn-extra-implicit", // Warn when more than one implicit parameter section is defined.
  "-Ywarn-inaccessible", // Warn about inaccessible types in method signatures.
  "-Ywarn-infer-any", // Warn when a type argument is inferred to be `Any`.
  "-Ywarn-nullary-override", // Warn when non-nullary `def f()' overrides nullary `def f'.
  "-Ywarn-nullary-unit", // Warn when nullary methods return Unit.
  "-Ywarn-numeric-widen", // Warn when numerics are widened.
  "-Ywarn-unused:implicits", // Warn if an implicit parameter is unused.
  "-Ywarn-unused:imports", // Warn if an import selector is not referenced.
  "-Ywarn-unused:locals", // Warn if a local definition is unused.
  "-Ywarn-unused:params", // Warn if a value parameter is unused.
  "-Ywarn-unused:patvars", // Warn if a variable bound in a pattern is unused.
  "-Ywarn-unused:privates", // Warn if a private member is unused.
  "-Ywarn-value-discard",  // Warn when non-Unit expression results are unused.
  "-Ypartial-unification", // Enable partial unification in type constructor inference

  //"-Xfatal-warnings",                  // Fail the compilation if there are any warnings.
  /*
   * These are flags specific to the "better-monadic-for" plugin:
   * https://github.com/oleg-py/better-monadic-for
   */
  "-P:bm4:no-filtering:y",
  "-P:bm4:no-map-id:y",
  "-P:bm4:no-tupling:y",
)

//=============================================================================
//=============================================================================

//https://github.com/busymachines/busymachines-commons
def bmCommons(m: String): ModuleID = "com.busymachines" %% s"busymachines-commons-$m" % "0.3.0-RC8"

lazy val bmcCore:          ModuleID = bmCommons("core")              withSources ()
lazy val bmcDuration:      ModuleID = bmCommons("duration")          withSources ()
lazy val bmcEffects:       ModuleID = bmCommons("effects")           withSources ()
lazy val bmcEffectsSync:   ModuleID = bmCommons("effects-sync")      withSources ()
lazy val bmcEffectsSyncC:  ModuleID = bmCommons("effects-sync-cats") withSources ()
lazy val bmcEffectsAsync:  ModuleID = bmCommons("effects-async")     withSources ()
lazy val bmcJson:          ModuleID = bmCommons("json")              withSources ()
lazy val bmcSemVer:        ModuleID = bmCommons("semver")            withSources ()
lazy val bmcSemVerParsers: ModuleID = bmCommons("semver-parsers")    withSources ()
//yucky akka stuff. Ew, stay away.
//lazy val bmcRestJson:      ModuleID = bmCommons("rest-json")         withSources ()
//lazy val bmcRestCore:      ModuleID = bmCommons("rest-core")         withSources ()
//lazy val bmcRestJsonTK:    ModuleID = bmCommons("rest-json-testkit") % Test withSources ()
//lazy val bmcRestCoreTK:    ModuleID = bmCommons("rest-core-testkit") % Test withSources ()

//============================================================================================
//================================= http://typelevel.org/scala/ ==============================
//========================================  typelevel ========================================
//============================================================================================

//https://github.com/typelevel/cats
lazy val catsCore: ModuleID = "org.typelevel" %% "cats-core" % "1.1.0" withSources ()

//https://github.com/typelevel/cats-effect
lazy val catsEffect: ModuleID = "org.typelevel" %% "cats-effect" % "0.10.1" withSources ()

//https://github.com/monix/monix
lazy val monix: ModuleID = "io.monix" %% "monix" % "3.0.0-RC1" withSources ()

//https://github.com/functional-streams-for-scala/fs2
lazy val fs2: ModuleID = "co.fs2" %% "fs2-core" % "0.10.4" withSources ()

//https://circe.github.io/circe/
lazy val circeVersion: String = "0.9.3"

lazy val circeCore:          ModuleID = "io.circe" %% "circe-core"           % circeVersion
lazy val circeGeneric:       ModuleID = "io.circe" %% "circe-generic"        % circeVersion
lazy val circeGenericExtras: ModuleID = "io.circe" %% "circe-generic-extras" % circeVersion

lazy val attoParser: ModuleID = "org.tpolecat" %% "atto-core" % "0.6.2" withSources ()

//https://github.com/http4s/http4s
lazy val Http4sVersion = "0.18.12"

lazy val http4sBlazeServer: ModuleID = "org.http4s" %% "http4s-blaze-server" % Http4sVersion withSources ()
lazy val http4sCirce:       ModuleID = "org.http4s" %% "http4s-circe"        % Http4sVersion withSources ()
lazy val http4sDSL:         ModuleID = "org.http4s" %% "http4s-dsl"          % Http4sVersion withSources ()

//https://github.com/tpolecat/doobie
lazy val doobieVersion = "0.5.3"

lazy val doobieHikari   = "org.tpolecat" %% "doobie-hikari"   % doobieVersion withSources () // HikariCP transactor.
lazy val doobiePostgres = "org.tpolecat" %% "doobie-postgres" % doobieVersion withSources () // Postgres driver 42.2.2 + type mappings.
lazy val doobieTK       = "org.tpolecat" %% "doobie-specs2"   % doobieVersion % Test withSources () // specs2 support for typechecking statements.

lazy val flyway = "org.flywaydb" % "flyway-core" % "4.2.0" withSources ()

lazy val shapeless: ModuleID = "com.chuusai" %% "shapeless" % "2.3.3" withSources ()

//============================================================================================
//==========================================  math ===========================================
//============================================================================================

lazy val spire: ModuleID = "org.typelevel" %% "spire" % "0.14.1" withSources ()

//============================================================================================
//========================================  security  ========================================
//============================================================================================

//https://github.com/jmcardon/tsec
lazy val tsecV = "0.0.1-M11"

lazy val tsec = Seq(
  "io.github.jmcardon" %% "tsec-common"        % tsecV withSources (),
  "io.github.jmcardon" %% "tsec-password"      % tsecV withSources (),
  "io.github.jmcardon" %% "tsec-cipher-jca"    % tsecV withSources (),
  "io.github.jmcardon" %% "tsec-cipher-bouncy" % tsecV withSources (),
  "io.github.jmcardon" %% "tsec-mac"           % tsecV withSources (),
  "io.github.jmcardon" %% "tsec-signatures"    % tsecV withSources (),
  "io.github.jmcardon" %% "tsec-hash-jca"      % tsecV withSources (),
  "io.github.jmcardon" %% "tsec-hash-bouncy"   % tsecV withSources (),
  "io.github.jmcardon" %% "tsec-libsodium"     % tsecV withSources (),
  "io.github.jmcardon" %% "tsec-jwt-mac"       % tsecV withSources (),
  "io.github.jmcardon" %% "tsec-jwt-sig"       % tsecV withSources (),
  "io.github.jmcardon" %% "tsec-http4s"        % tsecV withSources (),
)

//============================================================================================
//=========================================  logging =========================================
//============================================================================================
//https://github.com/ChristopherDavenport/log4cats
lazy val log4cats = "io.chrisdavenport" %% "log4cats-slf4j" % "0.0.6" withSources ()

//this is a Java library, notice that we used one single % instead of %%
//it is the backend implementation used by log4cats
lazy val logbackClassic = "ch.qos.logback" % "logback-classic" % "1.2.3" withSources ()

//============================================================================================
//==========================================  email ==========================================
//============================================================================================

//this is a Java library, notice that we used one single % instead of %%
lazy val javaxMail = "com.sun.mail" % "javax.mail" % "1.6.1" withSources ()

//============================================================================================
//========================================= html =============================================
//============================================================================================

//https://github.com/ruippeixotog/scala-scraper
lazy val scalaScrapper = "net.ruippeixotog" %% "scala-scraper" % "2.1.0" withSources ()

//============================================================================================
//=========================================  testing =========================================
//============================================================================================

//https://github.com/etorreborre/specs2
lazy val specs2: ModuleID = "org.specs2" %% "specs2-core" % "4.3.0" % Test withSources ()

//============================================================================================
//=========================================== misc ===========================================
//============================================================================================

//https://github.com/pureconfig/pureconfig
lazy val pureConfig: ModuleID = "com.github.pureconfig" %% "pureconfig" % "0.9.1" withSources ()
