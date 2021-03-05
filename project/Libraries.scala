import sbt._

object Libraries {
  //============================================================================================
  //============================================================================================
  //======================================= DEPENDENCIES =======================================
  //============================================================================================
  //============================================================================================

  lazy val pureHarmVersion       = "0.0.7-M3"  //https://github.com/busymachines/pureharm/releases
  lazy val catsCoreVersion       = "2.4.2"     //https://github.com/typelevel/cats/releases
  lazy val catsEffectVersion     = "2.3.3"     //https://github.com/typelevel/cats-effect/releases
  lazy val fs2Version            = "2.5.3"     //https://github.com/functional-streams-for-scala/fs2/releases
  lazy val circeVersion          = "0.13.0"    //https://github.com/circe/circe/releases
  lazy val http4sVersion         = "0.22.0-M4" //https://github.com/http4s/http4s/releases
  lazy val tsecVersion           = "0.2.1"     //https://github.com/jmcardon/tsec/releases
  lazy val doobieVersion         = "0.10.0"    //https://github.com/tpolecat/doobie/releases
  lazy val flywayVersion         = "7.6.0"     //https://github.com/flyway/flyway/releases
  lazy val shapelessVersion      = "2.3.3"     //https://github.com/milessabin/shapeless/releases
  lazy val spireVersion          = "0.17.0"    //https://github.com/non/spire/releases
  lazy val log4catsVersion       = "1.2.0"     //https://github.com/ChristopherDavenport/log4cats/releases
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

  lazy val cats:       ModuleID = "org.typelevel" %% "cats-core"   % catsCoreVersion   withSources ()
  lazy val catsEffect: ModuleID = "org.typelevel" %% "cats-effect" % catsEffectVersion withSources ()

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

  lazy val log4cats = "org.typelevel" %% "log4cats-slf4j" % log4catsVersion withSources ()

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

}
