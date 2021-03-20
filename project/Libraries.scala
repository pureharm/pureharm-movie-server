import sbt._

object Libraries {

  //============================================================================================
  //============================================================================================
  //======================================= DEPENDENCIES =======================================
  //============================================================================================
  //============================================================================================

  val sproutV         = "0.0.1"     //https://github.com/lorandszakacs/sprout/releases
  val pureharmCoreV   = "0.1.0"     //https://github.com/busymachines/pureharm/releases
  val catsCoreV       = "2.4.2"     //https://github.com/typelevel/cats/releases
  val catsEffectV     = "3.0.0-RC2" //https://github.com/typelevel/cats-effect/releases
  val fs2V            = "3.0.0-M9"  //https://github.com/typelevel/fs2/releases
  val circeV          = "0.13.0"    //https://github.com/circe/circe/releases
  val http4sV         = "1.0.0-M19" //https://github.com/http4s/http4s/releases
  val skunkV          = "0.1.0-M1"  //https://github.com/tpolecat/skunk/releases
  val flywayV         = "7.6.0"     //java - https://github.com/flyway/flyway/releases
  val pgJDBCV         = "42.2.19"   //java — https://github.com/pgjdbc/pgjdbc/releases
  val log4catsV       = "2.0.0-RC1" //https://github.com/typelevel/log4cats/releases
  val logbackClassicV = "1.2.3"     //https://github.com/qos-ch/logback/releases
  val javaxMailV      = "1.6.2"     // https://github.com/javaee/javamail/releases
  val scalaScrapperV  = "2.2.0"     //https://github.com/ruippeixotog/scala-scraper/releases
  val cirisV          = "2.0.0-RC1" //https://github.com/vlovgr/ciris/releases
  val javaBcryptV     = "0.9.0"     //java - https://github.com/patrickfav/bcrypt/releases
  val ips4sV          = "3.0.0-RC2" //https://github.com/Comcast/ip4s/releases

  val phCore = "com.busymachines"  %% s"pureharm-core" % pureharmCoreV withSources ()
  val sprout = "com.lorandszakacs" %% "sprout"         % sproutV       withSources ()

  //============================================================================================
  //================================= http://typelevel.org/scala/ ==============================
  //========================================  typelevel ========================================
  //============================================================================================
  val cats       = "org.typelevel" %% "cats-core"   % catsCoreV   withSources ()
  val catsEffect = "org.typelevel" %% "cats-effect" % catsEffectV withSources ()

  val fs2Core = "co.fs2" %% "fs2-core" % fs2V withSources ()
  val fs2IO   = "co.fs2" %% "fs2-io"   % fs2V withSources ()

  val circeCore          = "io.circe" %% "circe-core"           % circeV
  val circeGeneric       = "io.circe" %% "circe-generic"        % circeV
  val circeGenericExtras = "io.circe" %% "circe-generic-extras" % circeV

  val http4sEmberServer = "org.http4s" %% "http4s-ember-server" % http4sV withSources ()
  val http4sCirce       = "org.http4s" %% "http4s-circe"        % http4sV withSources ()
  val http4sDSL         = "org.http4s" %% "http4s-dsl"          % http4sV withSources ()

  val skunk  = "org.tpolecat"  %% "skunk-core"  % skunkV  withSources ()
  val flyway = "org.flywaydb"   % "flyway-core" % flywayV withSources ()
  val pgJDBC = "org.postgresql" % "postgresql"  % pgJDBCV withSources ()

  //============================================================================================
  //==========================================  math ===========================================
  //============================================================================================

  //============================================================================================
  //========================================  security  ========================================
  //============================================================================================

  val javaBcrypt = "at.favre.lib" % "bcrypt" % javaBcryptV withSources ()

  //============================================================================================
  //=========================================  logging =========================================
  //============================================================================================

  val log4cats = "org.typelevel" %% "log4cats-slf4j" % log4catsV withSources ()

  //this is a Java library, notice that we used one single % instead of %%
  //it is the backend implementation used by log4cats
  val logbackClassic = "ch.qos.logback" % "logback-classic" % logbackClassicV withSources ()

  //============================================================================================
  //==========================================  email ==========================================
  //============================================================================================

  //this is a Java library, notice that we used one single % instead of %%
  val javaxMail = "com.sun.mail" % "javax.mail" % javaxMailV withSources ()

  //============================================================================================
  //========================================= html =============================================
  //============================================================================================

  val scalaScrapper = "net.ruippeixotog" %% "scala-scraper" % scalaScrapperV withSources ()

  //============================================================================================
  //========================================== config ==========================================
  //============================================================================================

  val ciris = "is.cir" %% "ciris" % cirisV withSources ()

  val ip4s = "com.comcast" %% "ip4s-core" % ips4sV withSources ()

  //============================================================================================
  //=========================================  testing =========================================
  //============================================================================================

}
