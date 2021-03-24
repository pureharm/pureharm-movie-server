import sbt._
// really don't want to reformat the entire file if we change some version numbers
// it makes for really ugly git diffs that are hard to track
// format: off

/** We keep all our dependencies here, so that you can get an easy
  * overview of all external libraries needed to build a real server.
  *
  * As you can see, there's quite a lot of them.
  */

object Libraries {
  val sproutV           = "0.0.1"         //https://github.com/lorandszakacs/sprout/releases
  val pureharmCoreV     = "0.1.0"         //https://github.com/busymachines/pureharm/releases
  val catsCoreV         = "2.4.2"         //https://github.com/typelevel/cats/releases
  val catsEffectV       = "3.0.0-RC2"     //https://github.com/typelevel/cats-effect/releases
  val munitV            = "0.13.1"        //https://github.com/typelevel/munit-cats-effect/releases
  val fs2V              = "3.0.0-M9"      //https://github.com/typelevel/fs2/releases
  val circeV            = "0.13.0"        //https://github.com/circe/circe/releases
  val http4sV           = "1.0.0-M19"     //https://github.com/http4s/http4s/releases
  val skunkV            = "0.1.0-M1"      //https://github.com/tpolecat/skunk/releases
  val flywayV           = "7.6.0"         //java - https://github.com/flyway/flyway/releases
  val pgJDBCV           = "42.2.19"       //java — https://github.com/pgjdbc/pgjdbc/releases
  val log4catsV         = "2.0.0-RC1"     //https://github.com/typelevel/log4cats/releases
  val logbackClassicV   = "1.2.3"         //java - https://github.com/qos-ch/logback/releases
  val javaxMailV        = "1.6.2"         //https://github.com/javaee/javamail/releases
  val scalaScrapperV    = "2.2.0"         //https://github.com/ruippeixotog/scala-scraper/releases
  val cirisV            = "2.0.0-RC1"     //https://github.com/vlovgr/ciris/releases
  val ips4sV            = "3.0.0-RC2"     //https://github.com/Comcast/ip4s/releases
  val javaBcryptV       = "0.9.0"         //java - https://github.com/patrickfav/bcrypt/releases
  val scalaJavaTimeV    = "2.2.0"         //https://github.com/cquiroz/scala-java-time/releases
  
  val cats               = "org.typelevel"     %% "cats-core"             % catsCoreV        withSources() // foundational pure FP library
  val catsEffect         = "org.typelevel"     %% "cats-effect"           % catsEffectV      withSources() // pure FP library for concurrency, resource safety, and handling side-effects
  val fs2Core            = "co.fs2"            %% "fs2-core"              % fs2V             withSources() // pure FP streaming library
  val fs2IO              = "co.fs2"            %% "fs2-io"                % fs2V             withSources() // streaming support for the file system
  val phCore             = "com.busymachines"  %% s"pureharm-core"        % pureharmCoreV    withSources() // pureharm library - brings in anomalies, and glue for sprouts
  val sprout             = "com.lorandszakacs" %% "sprout"                % sproutV          withSources() // new-type encodings that allow us to remove stringly typed domain code
  val circeCore          = "io.circe"          %% "circe-core"            % circeV           withSources() // json library
  val circeGeneric       = "io.circe"          %% "circe-generic"         % circeV           withSources() // semi-automatic derivation of json codecs for case classes
  val http4sEmberServer  = "org.http4s"        %% "http4s-ember-server"   % http4sV          withSources() // backend server that handles all http connection stuff
  val http4sCirce        = "org.http4s"        %% "http4s-circe"          % http4sV          withSources() // integration between rest API and JSON library
  val http4sDSL          = "org.http4s"        %% "http4s-dsl"            % http4sV          withSources() // library used to define the rest API routes 
  val skunk              = "org.tpolecat"      %% "skunk-core"            % skunkV           withSources() // postgresql database driver written for cats-effect-3
  val flyway             = "org.flywaydb"       % "flyway-core"           % flywayV          withSources() // java - database schema migration tool
  val pgJDBC             = "org.postgresql"     % "postgresql"            % pgJDBCV          withSources() // java - "good" old Java stuff, required by flyway to interact w/ postgresql
  val ciris              = "is.cir"            %% "ciris"                 % cirisV           withSources() // config reading library
  val ip4s               = "com.comcast"       %% "ip4s-core"             % ips4sV           withSources() // config utility that brings in ports and host datatypes
  val javaBcrypt         = "at.favre.lib"       % "bcrypt"                % javaBcryptV      withSources() // java - library used to handle password hashes
  val scalaJavaTime      = "io.github.cquiroz" %% "scala-java-time"       % scalaJavaTimeV   withSources() // Scala implementation of the java.time package
  val log4cats           = "org.typelevel"     %% "log4cats-slf4j"        % log4catsV        withSources() // pure FP logging interface for cats-effect
  val logbackClassic     = "ch.qos.logback"     % "logback-classic"       % logbackClassicV  withSources() // java - implementation of logging that is used by the above
  val scalaScrapper      = "net.ruippeixotog"  %% "scala-scraper"         % scalaScrapperV   withSources() // html scraping library
  val javaxMail          = "com.sun.mail"       % "javax.mail"            % javaxMailV       withSources() // java - used for sending emails
  val munit              = "org.typelevel"     %% "munit-cats-effect-3"   % munitV           withSources() // testing library
}
