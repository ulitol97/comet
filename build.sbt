// See version in "version.sbt"
Global / name := "comet" // Friendly app name
ThisBuild / scalaVersion := "3.1.1"
Global / excludeLintKeys ++= Set(name, idePackagePrefix)

lazy val comet = (project in file("."))
  .enablePlugins(BuildInfoPlugin)
  .settings(
    name := "comet",
    idePackagePrefix := Some("org.ragna.comet"),
    resolverSettings,
    libraryDependencies ++= Seq(
      catsEffect,
      fs2,
      fs2Kafka,
      shexs,
      shaclex,
      wesoUtils,
      // testing
      scalaTest,
      catsEffectTesting
    ),
    buildInfoSettings,
    // https://stackoverflow.com/q/66372308/9744696
    Compile / run / fork := true
  )

// Aggregate resolver settings passed down to modules to resolve dependencies
// Helper to resolve dependencies from GitHub packages
lazy val resolverSettings = Seq(
  resolvers ++= Seq(
    Resolver.DefaultMavenRepository,
    Resolver.sonatypeRepo("snapshots")
  )
)

// Shared settings for the BuildInfo Plugin
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

// Core dependencies
lazy val catsVersion = "3.3.11"
// FS2 dependencies
lazy val fs2Version = "3.2.7"
lazy val fs2KafkaVersion = "3.0.0-M6"
// WESO dependencies
lazy val shaclexVersion = "0.2.2"
lazy val shexsVersion = "0.2.2"
lazy val umlShaclexVersion = "0.0.82"
lazy val wesoUtilsVersion = "0.2.4"
// Testing dependencies
lazy val scalaTestVersion = "3.2.11" // Usual testing
lazy val catsEffectTestingVersion = "1.4.0" // Integration of ScalaTest with cats-effect

// -------------------------------------------------------------------------- //

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
lazy val catsEffectTesting = "org.typelevel" %% "cats-effect-testing-scalatest" % catsEffectTestingVersion % Test