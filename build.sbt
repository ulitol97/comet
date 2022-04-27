

/* ------------------------------------------------------------------------- */
/* BASIC PROPERTIES */
// See version in "version.sbt"
Global / name := "comet" // Friendly app name
ThisBuild / scalaVersion := "3.1.2"
Global / excludeLintKeys ++= Set(name, idePackagePrefix)

/* ------------------------------------------------------------------------- */
/* MODULES */

// Root project
lazy val root = (project in file("."))
  .aggregate(comet)

// Comet's core API, to be published
lazy val comet = (project in file("api"))
  .enablePlugins(BuildInfoPlugin)
  .settings(
    name := "comet",
    idePackagePrefix := Some("org.ragna.comet"),
    Compile / run / fork := true, // https://stackoverflow.com/q/66372308/9744696
    resolverSettings,
    testFrameworks += ScalaTestFramework,
    buildInfoSettings,
    libraryDependencies ++= Seq(
      catsEffect,
      fs2,
      fs2Kafka,
      shexs,
      shaclex,
      wesoUtils,
      // testing
      scalaTest,
      catsEffectTesting,
      // logging
      scalaLogging
    )
  )

/* ------------------------------------------------------------------------- */
/* RESOLVER SETTINGS */
// Settings passed down to modules to resolve dependencies
lazy val resolverSettings = Seq(
  resolvers ++= Seq(
    Resolver.DefaultMavenRepository, // maven
    Resolver.sonatypeRepo("snapshots") // sonatype
  )
)

/* ------------------------------------------------------------------------- */
/* PUBLISH SETTINGS */
// Shared publish settings for all modules.
// The module is pushed to Sonatype in CI
// See https://github.com/sbt/sbt-ci-release
ThisBuild / organization := "com.ragna"
ThisBuild / homepage := Some(url("https://github.com/ulitol97"))
ThisBuild / licenses := List("MIT" -> url("https://mit-license.org/"))
ThisBuild / developers := List(
  Developer(
    "ragna",
    "Eduardo Ulibarri Toledo",
    "eduulitol@protonmail.com",
    url("https://github.com/ulitol97")
  )
)
// Override sonatype server location (accounts created after Feb 2021)
sonatypeCredentialHost := "s01.oss.sonatype.org"
sonatypeRepository := "https://s01.oss.sonatype.org/service/local"

/* ------------------------------------------------------------------------- */
/* BUILD INFO */
// Shared settings for the BuildInfo Plugin
// Allows access to apps metadata in runtime code
// See https://github.com/sbt/sbt-buildinfo
lazy val buildInfoSettings = Seq(
  buildInfoKeys := Seq[BuildInfoKey](
    name,
    version,
    scalaVersion,
    sbtVersion
  ),
  buildInfoPackage := "buildinfo",
  buildInfoObject := "BuildInfo"
)

/* PACKAGING SETTINGS */
// Shared packaging settings for all modules
lazy val packagingSettings = Seq(
  // Do not package logback files in .jar, they interfere with other logback
  // files in classpath
  Compile / packageBin / mappings ~= { project =>
    project.filter { case (file, _) =>
      val fileName = file.getName
      !(fileName.startsWith("logback") && (fileName.endsWith(".xml") || fileName
        .endsWith(".groovy")))
    }
  }
)

/* ------------------------------------------------------------------------- */
/* SBT GITHUB ACTIONS */
// GitHub Actions for build/test and clean are automatically generated
// The settings of these actions are configured here:
//  - Scala versions used: scala 3 (scala 2 won't compile Scala 3 code)
//  - Java versions used: LST 11, LTS 17
//  - etc.
// See https://github.com/djspiewak/sbt-github-actions
lazy val scala3 = "3.1.2"
lazy val ciScalaVersions = List(scala3)

lazy val java11 = JavaSpec.temurin("11")
lazy val java17 = JavaSpec.temurin("17")
lazy val ciJavaVersions = List(java11, java17)

// Specify which versions to be included in the GitHub Actions matrix when
// created by `githubWorkflowGenerate`
ThisBuild / crossScalaVersions := ciScalaVersions
ThisBuild / githubWorkflowJavaVersions := ciJavaVersions

/* ------------------------------------------------------------------------- */
/* TEST FRAMEWORKS */
// https://www.scala-sbt.org/1.x/docs/Testing.html
lazy val ScalaTestFramework = new TestFramework("scalatest.Framework")

/* ------------------------------------------------------------------------- */
/* DEPENDENCY VERSIONS */
// Core dependencies
lazy val catsVersion = "3.3.11"
// FS2 dependencies
lazy val fs2Version = "3.2.7"
lazy val fs2KafkaVersion = "3.0.0-M7"
// WESO dependencies
lazy val shaclexVersion = "0.2.2"
lazy val shexsVersion = "0.2.2"
lazy val umlShaclexVersion = "0.0.82"
lazy val wesoUtilsVersion = "0.2.4"
// Testing dependencies
lazy val scalaTestVersion = "3.2.11" // Usual testing
lazy val catsEffectTestingVersion = "1.4.0" // Integration of ScalaTest with cats-effect
// Other
lazy val scalaLoggingVersion = "3.9.4"

/* ------------------------------------------------------------------------- */
/* DEPENDENCIES */
// Core dependencies
lazy val catsEffect = "org.typelevel" %% "cats-effect" % catsVersion
// FS2 dependencies
lazy val fs2 = "co.fs2" %% "fs2-core" % fs2Version
lazy val fs2Kafka = "com.github.fd4s" %% "fs2-kafka" % fs2KafkaVersion
// WESO dependencies
lazy val shexs = "es.weso" %% "shexs" % shexsVersion
lazy val shaclex = "es.weso" %% "shaclex" % shaclexVersion
lazy val wesoUtils = "es.weso" %% "utilstest" % wesoUtilsVersion
// Testing dependencies
lazy val scalaTest = "org.scalatest" %% "scalatest" % scalaTestVersion % Test
lazy val catsEffectTesting =
  "org.typelevel" %% "cats-effect-testing-scalatest" % catsEffectTestingVersion % Test
// Other
lazy val scalaLogging =
  "com.typesafe.scala-logging" %% "scala-logging" % scalaLoggingVersion