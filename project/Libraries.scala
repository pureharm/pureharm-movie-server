import sbt._

object Libraries {
  //============================================================================================
  //============================================================================================
  //======================================= DEPENDENCIES =======================================
  //============================================================================================
  //============================================================================================

  lazy val sproutV         = "0.0.1"     //https://github.com/lorandszakacs/sprout/releases
  lazy val pureharmV       = "0.0.7-M3"  //https://github.com/busymachines/pureharm/releases
  lazy val catsCoreV       = "2.4.2"     //https://github.com/typelevel/cats/releases
  lazy val catsEffectV     = "2.3.3"     //https://github.com/typelevel/cats-effect/releases
  lazy val fs2V            = "2.5.3"     //https://github.com/functional-streams-for-scala/fs2/releases
  lazy val circeV          = "0.13.0"    //https://github.com/circe/circe/releases
  lazy val http4sV         = "0.22.0-M4" //https://github.com/http4s/http4s/releases
  lazy val tsecV           = "0.2.1"     //https://github.com/jmcardon/tsec/releases
  lazy val doobieV         = "0.10.0"    //https://github.com/tpolecat/doobie/releases
  lazy val flywayV         = "7.6.0"     //https://github.com/flyway/flyway/releases
  lazy val shapelessV      = "2.3.3"     //https://github.com/milessabin/shapeless/releases
  lazy val spireV          = "0.17.0"    //https://github.com/non/spire/releases
  lazy val log4catsV       = "1.2.0"     //https://github.com/ChristopherDavenport/log4cats/releases
  lazy val logbackClassicV = "1.2.3"     //https://github.com/qos-ch/logback/releases
  lazy val javaxMailV      = "1.6.2"     // https://github.com/javaee/javamail/releases
  lazy val scalaScrapperV  = "2.2.0"     //https://github.com/ruippeixotog/scala-scraper/releases
  lazy val pureConfigV     = "0.12.3"    //https://github.com/pureconfig/pureconfig/releases
  lazy val specs2V         = "4.9.4"     //https://github.com/etorreborre/specs2/releases

  def pureharm(m: String): ModuleID = "com.busymachines" %% s"pureharm-$m" % pureharmV withSources ()

  lazy val phCore:    ModuleID = pureharm("core")
  lazy val phConfig:  ModuleID = pureharm("config")
  lazy val phJson:    ModuleID = pureharm("json-circe")
  lazy val phFlyway:  ModuleID = pureharm("db-core-flyway")

  lazy val sprout: ModuleID = "com.lorandszakacs" %% "sprout" % sproutV withSources ()

  //============================================================================================
  //================================= http://typelevel.org/scala/ ==============================
  //========================================  typelevel ========================================
  //============================================================================================

  lazy val cats:       ModuleID = "org.typelevel" %% "cats-core"   % catsCoreV   withSources ()
  lazy val catsEffect: ModuleID = "org.typelevel" %% "cats-effect" % catsEffectV withSources ()

  lazy val fs2Core: ModuleID = "co.fs2" %% "fs2-core" % fs2V withSources ()
  lazy val fs2IO:   ModuleID = "co.fs2" %% "fs2-io"   % fs2V withSources ()

  lazy val fs2: Seq[ModuleID] = Seq(fs2Core, fs2IO)

  lazy val circeCore:          ModuleID = "io.circe" %% "circe-core"           % circeV
  lazy val circeGeneric:       ModuleID = "io.circe" %% "circe-generic"        % circeV
  lazy val circeGenericExtras: ModuleID = "io.circe" %% "circe-generic-extras" % circeV

  lazy val circe: Seq[ModuleID] = Seq(circeCore, circeGeneric, circeGenericExtras)

  lazy val http4sBlazeServer: ModuleID = "org.http4s" %% "http4s-blaze-server" % http4sV withSources ()
  lazy val http4sCirce:       ModuleID = "org.http4s" %% "http4s-circe"        % http4sV withSources ()
  lazy val http4sDSL:         ModuleID = "org.http4s" %% "http4s-dsl"          % http4sV withSources ()

  lazy val http4s: Seq[ModuleID] = Seq(http4sBlazeServer, http4sCirce, http4sDSL)

  lazy val doobieCore     = "org.tpolecat" %% "doobie-core"     % doobieV withSources ()
  lazy val doobieHikari   = "org.tpolecat" %% "doobie-hikari"   % doobieV withSources ()
  lazy val doobiePostgres = "org.tpolecat" %% "doobie-postgres" % doobieV withSources ()
  lazy val doobieTK       = "org.tpolecat" %% "doobie-specs2"   % doobieV           % Test withSources ()

  lazy val doobie: Seq[ModuleID] = Seq(doobieCore, doobieHikari, doobiePostgres, doobieTK)

  lazy val shapeless: ModuleID = "com.chuusai" %% "shapeless" % shapelessV withSources ()

  lazy val flyway = "org.flywaydb" % "flyway-core" % flywayV withSources ()

  //============================================================================================
  //==========================================  math ===========================================
  //============================================================================================

  lazy val spire: ModuleID = "org.typelevel" %% "spire" % spireV withSources ()

  //============================================================================================
  //========================================  security  ========================================
  //============================================================================================

  lazy val tsec: Seq[ModuleID] = Seq(
    "io.github.jmcardon" %% "tsec-common"   % tsecV withSources (),
    "io.github.jmcardon" %% "tsec-password" % tsecV withSources (),
    "io.github.jmcardon" %% "tsec-mac"      % tsecV withSources (),
    "io.github.jmcardon" %% "tsec-jwt-mac"  % tsecV withSources (),
  )

  //============================================================================================
  //=========================================  logging =========================================
  //============================================================================================

  lazy val log4cats = "org.typelevel" %% "log4cats-slf4j" % log4catsV withSources ()

  //this is a Java library, notice that we used one single % instead of %%
  //it is the backend implementation used by log4cats
  lazy val logbackClassic = "ch.qos.logback" % "logback-classic" % logbackClassicV withSources ()

  //============================================================================================
  //==========================================  email ==========================================
  //============================================================================================

  //this is a Java library, notice that we used one single % instead of %%
  lazy val javaxMail = "com.sun.mail" % "javax.mail" % javaxMailV withSources ()

  //============================================================================================
  //========================================= html =============================================
  //============================================================================================

  lazy val scalaScrapper = "net.ruippeixotog" %% "scala-scraper" % scalaScrapperV withSources ()

  //============================================================================================
  //========================================== config ==========================================
  //============================================================================================

  lazy val pureConfig: ModuleID = "com.github.pureconfig" %% "pureconfig" % pureConfigV withSources ()

  //============================================================================================
  //=========================================  testing =========================================
  //============================================================================================

  lazy val specs2: ModuleID = "org.specs2" %% "specs2-core" % specs2V withSources ()

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
