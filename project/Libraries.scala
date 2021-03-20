import sbt._

object Libraries {

  //============================================================================================
  //============================================================================================
  //======================================= DEPENDENCIES =======================================
  //============================================================================================
  //============================================================================================

  lazy val sproutV         = "0.0.1"     //https://github.com/lorandszakacs/sprout/releases
  lazy val pureharmCoreV   = "0.0.7-M3"  //https://github.com/busymachines/pureharm/releases
  lazy val catsCoreV       = "2.4.2"     //https://github.com/typelevel/cats/releases
  lazy val catsEffectV     = "3.0.0-RC2" //https://github.com/typelevel/cats-effect/releases
  lazy val fs2V            = "3.0.0-M9"  //https://github.com/typelevel/fs2/releases
  lazy val circeV          = "0.13.0"    //https://github.com/circe/circe/releases
  lazy val http4sV         = "1.0.0-M19" //https://github.com/http4s/http4s/releases
  lazy val skunkV          = "0.1.0-M1"  //https://github.com/tpolecat/skunk/releases
  lazy val flywayV         = "7.6.0"     //java - https://github.com/flyway/flyway/releases
  lazy val log4catsV       = "2.0.0-RC1" //https://github.com/typelevel/log4cats/releases
  lazy val logbackClassicV = "1.2.3"     //https://github.com/qos-ch/logback/releases
  lazy val javaxMailV      = "1.6.2"     // https://github.com/javaee/javamail/releases
  lazy val scalaScrapperV  = "2.2.0"     //https://github.com/ruippeixotog/scala-scraper/releases
  lazy val cirisV          = "2.0.0-RC1" //https://github.com/vlovgr/ciris/releases
  lazy val javaBcryptV     = "0.9.0"     //java - https://github.com/patrickfav/bcrypt/releases

  lazy val phCore: ModuleID = "com.busymachines" %% s"pureharm-core" % pureharmCoreV withSources ()

  lazy val sprout: ModuleID = "com.lorandszakacs" %% "sprout" % sproutV withSources ()

  //============================================================================================
  //================================= http://typelevel.org/scala/ ==============================
  //========================================  typelevel ========================================
  //============================================================================================

  lazy val cats:       ModuleID = "org.typelevel" %% "cats-core"   % catsCoreV   withSources ()
  lazy val catsEffect: ModuleID = "org.typelevel" %% "cats-effect" % catsEffectV withSources ()

  lazy val fs2Core: ModuleID = "co.fs2" %% "fs2-core" % fs2V withSources ()
  lazy val fs2IO:   ModuleID = "co.fs2" %% "fs2-io"   % fs2V withSources ()

  lazy val circeCore:          ModuleID = "io.circe" %% "circe-core"           % circeV
  lazy val circeGeneric:       ModuleID = "io.circe" %% "circe-generic"        % circeV
  lazy val circeGenericExtras: ModuleID = "io.circe" %% "circe-generic-extras" % circeV

  lazy val http4sBlazeServer: ModuleID = "org.http4s" %% "http4s-blaze-server" % http4sV withSources ()
  lazy val http4sCirce:       ModuleID = "org.http4s" %% "http4s-circe"        % http4sV withSources ()
  lazy val http4sDSL:         ModuleID = "org.http4s" %% "http4s-dsl"          % http4sV withSources ()

  lazy val skunk  = "org.tpolecat" %% "skunk-core"  % skunkV  withSources ()
  lazy val flyway = "org.flywaydb"  % "flyway-core" % flywayV withSources ()

  //============================================================================================
  //==========================================  math ===========================================
  //============================================================================================

  //============================================================================================
  //========================================  security  ========================================
  //============================================================================================

  lazy val javaBcrypt = "at.favre.lib" % "bcrypt" % javaBcryptV withSources ()

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

  lazy val ciris: ModuleID = "is.cir" %% "ciris" % cirisV withSources ()

  //============================================================================================
  //=========================================  testing =========================================
  //============================================================================================

}
