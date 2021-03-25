/*
 * Copyright 2021 BusyMachines
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import sbt._
import Settings._

addCommandAlias("mkJar", ";clean;update;compile;phms-app-server/stage")

//=============================================================================
//=============================================================================
//=============================================================================

Global / organization     := "com.busymachines"
Global / organizationName := "BusyMachines"
Global / homepage         := Option(url("https://www.busymachines.com/"))
Global / startYear        := Some(2021)
Global / licenses         := List("Apache-2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0"))

//=============================================================================
//=============================================================================
//=============================================================================

lazy val root = Project(id = "pureharm-movie-server", file("."))
  .settings(commonSettings)
  .aggregate(
    `phms-util-core`,
    `phms-util-testkit`,
    `phms-util-kernel`,
    `phms-util-logger`,
    `phms-util-config`,
    `phms-util-json`,
    `phms-util-http`,
    `phms-util-db-config`,
    `phms-util-db`,
    `phms-util-time`,
    `phms-port-email`,
    `phms-algebra-user`,
    `phms-algebra-movie`,
    `phms-algebra-imdb`,
    `phms-stack-http-sec`,
    `phms-organizer-movie`,
    `phms-organizer-user`,
    `phms-api-user`,
    `phms-api-movie`,
    `phms-app-bootstrap`,
    `phms-app-server`,
  )

//=============================================================================
//=================================== APPS ====================================
//=============================================================================

lazy val `phms-app-server` = appProject("server")
  .settings(commonSettings)
  .enablePlugins(JavaAppPackaging)
  .settings(
    Compile / mainClass             := Option("phms.server.PHMSMain"),
    Compile / discoveredMainClasses := Seq.empty, // see https://sbt-native-packager.readthedocs.io/en/stable/archetypes/java_app/index.html#java-app-plugin
  )
  .settings(
    libraryDependencies ++= Seq(
      Libraries.http4sEmberServer
    )
  )
  .dependsOn(
    `phms-util-core`,
    `phms-util-logger`,
    `phms-util-config`,
    `phms-util-db-config`,
    `phms-util-http`,
    `phms-organizer-user`,
    `phms-organizer-movie`,
    `phms-api-user`,
    `phms-api-movie`,
    `phms-app-bootstrap`,
    asTestingLibrary(`phms-util-testkit`),
  )

lazy val `phms-app-bootstrap` = appProject("bootstrap")
  .settings(commonSettings)
  .settings(
    libraryDependencies ++= Seq()
  )
  .dependsOn(
    `phms-util-logger`,
    `phms-util-core`,
    `phms-util-config`,
    `phms-util-core`,
    `phms-util-db-config`,
    `phms-algebra-user`,
    asTestingLibrary(`phms-util-testkit`),
  )
  .aggregate(
    `phms-util-logger`,
    `phms-util-core`,
    `phms-util-config`,
    `phms-util-db-config`,
    `phms-util-core`,
    `phms-algebra-user`,
  )

//=============================================================================
//=================================== APIS ====================================
//=============================================================================

lazy val `phms-api-movie` = apiProject("movie")
  .settings(
    libraryDependencies ++= Seq(
    )
  )
  .dependsOn(
    `phms-algebra-user`,
    `phms-organizer-movie`,
    `phms-algebra-movie`,
    `phms-stack-http-sec`,
    `phms-util-config`,
    `phms-util-logger`,
    `phms-util-core`,
    `phms-util-json`,
    `phms-util-http`,
    asTestingLibrary(`phms-util-testkit`),
  )

lazy val `phms-api-user` = apiProject("user")
  .dependsOn(
    `phms-algebra-user`,
    `phms-organizer-user`,
    `phms-stack-http-sec`,
    `phms-port-email`,
    `phms-util-config`,
    `phms-util-logger`,
    `phms-util-core`,
    `phms-util-json`,
    `phms-util-http`,
    asTestingLibrary(`phms-util-testkit`),
  )

//=============================================================================
//================================ ORGANIZERS =================================
//=============================================================================

lazy val `phms-organizer-user` = organizerProject("user")
  .settings(
    libraryDependencies ++= Seq(
    )
  )
  .dependsOn(
    `phms-algebra-user`,
    `phms-stack-http-sec`,
    `phms-port-email`,
    `phms-util-config`,
    `phms-util-logger`,
    `phms-util-core`,
    `phms-util-core`,
    `phms-util-json`,
    `phms-util-http`,
    asTestingLibrary(`phms-util-testkit`),
  )

lazy val `phms-organizer-movie` = organizerProject("movie")
  .dependsOn(
    `phms-algebra-user`,
    `phms-algebra-imdb`,
    `phms-algebra-movie`,
    `phms-stack-http-sec`,
    `phms-util-config`,
    `phms-util-logger`,
    `phms-util-core`,
    `phms-util-core`,
    `phms-util-json`,
    `phms-util-http`,
    asTestingLibrary(`phms-util-testkit`),
  )
//=============================================================================
//================================== STACKS ===================================
//=============================================================================

lazy val `phms-stack-http-sec` = stackProject("http-sec")
  .dependsOn(
    `phms-util-config`,
    `phms-util-core`,
    `phms-util-http`,
    `phms-algebra-user`,
    asTestingLibrary(`phms-util-testkit`),
  )

//=============================================================================
//================================= ALGEBRAS ==================================
//=============================================================================

lazy val `phms-algebra-imdb` = algebraProject("imdb")
  .settings(
    libraryDependencies ++= Seq(
      Libraries.scalaScrapper
    )
  )
  .dependsOn(
    `phms-util-config`,
    `phms-util-logger`,
    `phms-util-core`,
    asTestingLibrary(`phms-util-testkit`),
  )

lazy val `phms-algebra-movie` = algebraProject("movie")
  .dependsOn(
    `phms-algebra-user`,
    `phms-util-config`,
    `phms-util-core`,
    `phms-util-db`,
    asTestingLibrary(`phms-util-testkit`),
  )

lazy val `phms-algebra-user` = algebraProject("user")
  .dependsOn(
    `phms-util-config`,
    `phms-util-core`,
    `phms-port-email`,
    `phms-util-db`,
    `phms-util-crypto`,
    `phms-util-kernel`,
    `phms-util-time`,
    asTestingLibrary(`phms-util-testkit`),
  )

//=============================================================================
//================================== PORTS ====================================
//=============================================================================

lazy val `phms-port-email` = portProject("email")
  .settings(
    libraryDependencies ++= Seq(
      Libraries.javaxMail,
      Libraries.ip4s,
    )
  )
  .dependsOn(
    `phms-util-core`,
    `phms-util-logger`,
    `phms-util-config`,
    `phms-util-kernel`,
    asTestingLibrary(`phms-util-testkit`),
  )

//=============================================================================
//================================== TROVES ===================================
//=============================================================================

//=============================================================================
//================================== UTILS ====================================
//=============================================================================

lazy val `phms-util-time` = utilProject("time")
  .settings(
    libraryDependencies ++= Seq(
      Libraries.scalaJavaTime
    )
  )
  .dependsOn(
    `phms-util-core`,
    asTestingLibrary(`phms-util-testkit`),
  )

lazy val `phms-util-db` = utilProject("db")
  .settings(
    libraryDependencies ++= Seq(
      Libraries.skunk
    )
  )
  .dependsOn(
    `phms-util-core`,
    `phms-util-db-config`,
    `phms-util-logger`,
    `phms-util-kernel`,
    asTestingLibrary(`phms-util-testkit`),
  )

lazy val `phms-util-db-config` = utilProject("db-config")
  .settings(
    libraryDependencies ++= Seq(
      Libraries.flyway,
      Libraries.pgJDBC,
      Libraries.ip4s,
    )
  )
  .dependsOn(
    `phms-util-config`,
    `phms-util-core`,
  )

lazy val `phms-util-http` = utilProject("http")
  .settings(
    libraryDependencies ++= Seq(
      Libraries.http4sDSL,
      Libraries.http4sServer,
      Libraries.http4sCirce,
    )
  )
  .dependsOn(
    `phms-util-core`,
    `phms-util-json`,
    asTestingLibrary(`phms-util-testkit`),
  )

lazy val `phms-util-json` = utilProject("json")
  .settings(
    libraryDependencies ++= Seq(
      Libraries.circeCore,
      Libraries.circeGeneric,
    )
  )
  .dependsOn(
    `phms-util-core`,
    `phms-util-kernel`,
    `phms-util-time`,
  )

lazy val `phms-util-config` = utilProject("config")
  .settings(
    libraryDependencies ++= Seq(
      Libraries.ciris,
      Libraries.ip4s,
    )
  )
  .dependsOn(
    `phms-util-core`
  )

lazy val `phms-util-logger` = utilProject("logger")
  .settings(
    libraryDependencies ++= Seq(
      Libraries.log4cats,
      Libraries.logbackClassic,
    )
  )
  .dependsOn(
    `phms-util-core`
  )

lazy val `phms-util-crypto` = utilProject("crypto")
  .settings(
    libraryDependencies ++= Seq(
      Libraries.javaBcrypt
    )
  )
  .dependsOn(
    `phms-util-core`,
    `phms-util-kernel`,
    asTestingLibrary(`phms-util-testkit`),
  )

lazy val `phms-util-kernel` = utilProject("kernel")
  .settings(
    libraryDependencies ++= Seq()
  )
  .dependsOn(
    `phms-util-core`
  )

lazy val `phms-util-testkit` = utilProject("testkit")
  .settings(
    libraryDependencies ++= Seq(
      Libraries.munit
    )
  )
  .dependsOn(
    `phms-util-core`
  )

lazy val `phms-util-core` = utilProject("core")
  .settings(
    libraryDependencies ++= Seq(
      Libraries.sprout,
      Libraries.phCore,
      Libraries.cats,
      Libraries.catsEffect,
      Libraries.fs2Core,
    )
  )

//=============================================================================
//=============================================================================
// format: off
def utilProject      (name: String): Project = Project(s"phms-util-$name",      file(s"modules/utils/$name"))     .settings(commonSettings)
def troveProject     (name: String): Project = Project(s"phms-trove-$name",     file(s"modules/troves/$name"))    .settings(commonSettings)
def portProject      (name: String): Project = Project(s"phms-port-$name",      file(s"modules/ports/$name"))     .settings(commonSettings)
def algebraProject   (name: String): Project = Project(s"phms-algebra-$name",   file(s"modules/algebras/$name"))  .settings(commonSettings)
def stackProject     (name: String): Project = Project(s"phms-stack-$name"  ,   file(s"modules/stacks/$name"))    .settings(commonSettings)
def organizerProject (name: String): Project = Project(s"phms-organizer-$name", file(s"modules/organizers/$name")).settings(commonSettings)
def apiProject       (name: String): Project = Project(s"phms-api-$name",       file(s"modules/apis/$name"))      .settings(commonSettings)
def appProject       (name: String): Project = Project(s"phms-app-$name",       file(s"modules/apps/$name"))      .settings(commonSettings)
// format: on
/** See SBT docs:
  * https://www.scala-sbt.org/release/docs/Multi-Project.html#Per-configuration+classpath+dependencies
  *
  * or an example:
  * {{{
  * val testModule = project
  *
  * val prodModule = project
  *   .dependsOn(asTestingLibrary(testModule))
  * }}}
  * To ensure that testing code and dependencies
  * do not wind up in the "compile" (i.e.) prod part of your
  * application.
  */
def asTestingLibrary(p:     Project): ClasspathDependency = p % "test -> compile"
