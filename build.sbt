/* ------------------------------------------------------------------------- */
/* BASIC PROPERTIES */
// See version in "version.sbt"

// Friendly app name
Global / name := "comet"
// Scala version used in development
ThisBuild / scalaVersion := "3.1.2"

// Lint exclusions
Global / excludeLintKeys ++= Set(name, idePackagePrefix)

/* ------------------------------------------------------------------------- */
/* (CROSS) COMPILATION SETTINGS */
// Define Scala/Java reusable constants
lazy val scala2_13 = "2.13.8"
lazy val scala3_1 = "3.1.2"
lazy val java11 = JavaSpec.temurin("11")
lazy val java17 = JavaSpec.temurin("17")

// Although the library is developed and tested for Scala 3, we cross-compile
// to Scala 2 as well, to reach a wider target
// See https://www.scala-sbt.org/1.x/docs/Cross-Build.html
lazy val supportedScalaVersions = List(scala2_13, scala3_1)

// Helpers
lazy val scalaVersionPattern = """\..{1,2}$""".r

/* ------------------------------------------------------------------------- */
/* MODULES */

// Root project
lazy val comet = (project in file("."))
  .aggregate(api)
  .settings(
    name := "root",
    // crossScalaVersions must be set to Nil on the root project
    crossScalaVersions := Nil,
    // Publishing disabled in root project
    publish / skip := true,
    // Common settings
    buildInfoSettings,
    packagingSettings,
    resolverSettings,
    scaladocSettings,
    // Excluded dependencies
    excludeDependencies ++= Seq(
      // Exclude slf4j backend if present in other dependencies to avoid
      // warnings/conflicts with logback
      ExclusionRule("org.slf4j", "slf4j-simple")
    )
  )
  .enablePlugins(BuildInfoPlugin)

// Comet's core API, to be published
lazy val api = (project in file("api"))
  .enablePlugins(BuildInfoPlugin)
  .settings(
    name := "comet",
    idePackagePrefix := Some("org.ragna.comet"),
    Compile / run / fork := true, // https://stackoverflow.com/q/66372308/9744696
    crossScalaVersions := supportedScalaVersions, // cross-compile
    // Common settings
    buildInfoSettings,
    packagingSettings,
    resolverSettings,
    scaladocSettings,
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

// Documentation project, for MDoc + Docusaurus documentation
lazy val docs = project
  .in(file("comet-docs"))
  .enablePlugins(MdocPlugin, DocusaurusPlugin, ScalaUnidocPlugin)
  .settings(
    // Publishing disabled in docs project
    publish / skip := true,
    // Pre-existing settings
    unidocSettings,
    mdocSettings,
    // Custom settings
    name := s"${(Global / name).value}-api-docs",
    moduleName := s"${(Global / name).value}-api-docs"
  )

/* ------------------------------------------------------------------------- */
/* RESOLVER SETTINGS */
// Settings passed down to modules to resolve dependencies
lazy val resolverSettings = Seq(
  resolvers ++= Seq(
    Resolver.DefaultMavenRepository, // maven
    Opts.resolver.sonatypeSnapshots, // sonatype
    Opts.resolver.sonatypeReleases
  )
)

/* ------------------------------------------------------------------------- */
/* PUBLISH SETTINGS */
// Shared publish settings for all modules.
// The module is pushed to Sonatype in CI (cross published for cross scala versions) 
// See https://github.com/sbt/sbt-ci-release
ThisBuild / organization := "io.github.ulitol97"
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
//  - Scala versions used: scala 2.13, scala 3 (cross compiled)
//  - Java versions used: LST 11, LTS 17
//  - etc.
// See https://github.com/djspiewak/sbt-github-actions
lazy val ciScalaVersions = List(scala2_13, scala3_1)
lazy val ciJavaVersions = List(java11, java17)

// Specify which versions to be included in the GitHub Actions matrix when
// created by `githubWorkflowGenerate`
ThisBuild / githubWorkflowScalaVersions := ciScalaVersions
ThisBuild / githubWorkflowJavaVersions := ciJavaVersions

// Do not try to publish when building and testing, there's another action for that
ThisBuild / githubWorkflowPublishTargetBranches := Seq()

/* ------------------------------------------------------------------------- */
/* SCALADOC SETTINGS */
// Scaladoc settings for docs generation. Run task "doc" or "comet / doc".
// https://www.scala-sbt.org/1.x/docs/Howto-Scaladoc.html
// https://github.com/lampepfl/dotty/blob/main/scaladoc/src/dotty/tools/scaladoc/ScaladocSettings.scala#L62-L63
// https://github.com/scala/scala/blob/2.13.x/src/scaladoc/scala/tools/nsc/doc/Settings.scala
lazy val scaladocSettings: Seq[Def.Setting[_]] = Seq(
  // Generate documentation on a separated "docs" folder
  Compile / doc / target := baseDirectory.value / "target" / "scaladoc",
  Compile / doc / scalacOptions ++= Seq(
    // Base source path
    "-sourcepath",
    (LocalRootProject / baseDirectory).value.getAbsolutePath,
    // Link to GitHub source
    "-doc-source-url",
    scmInfo.value.get.browseUrl + "/tree/master€{FILE_PATH}.scala",
    // Page title
    "-doc-title",
    "Comet API - Docs",
    // Docs version
    "-doc-version",
    version.value,
    // Docs footer
    "-doc-footer",
    "By ulitol97 - <3",
    // Skip unnecessary source generated by BuildInfo plugin
    "-skip-by-id:buildinfo",
    // Other settings: include private members, etc.
    "-private",
    "-no-link-warnings"
  ),
  // Need to generate docs to publish to OSS
  Compile / packageDoc / publishArtifact := true
)

/* ------------------------------------------------------------------------- */
/* UNIDOC SETTINGS */
// Unidoc is used to generate Scaladoc for all modules and place it in a custom
// location inside the project's microsite
// See https://github.com/sbt/sbt-unidoc
lazy val unidocSettings: Seq[Def.Setting[_]] = Seq(
  // Generate docs for the API module
  ScalaUnidoc / unidoc / unidocProjectFilter := inProjects(api),
  // Dump docs into the website static part, to link them with docusaurus
  ScalaUnidoc / unidoc / target := (LocalRootProject / baseDirectory).value / websiteFolder / "static" / "scaladoc",
  // When cleaning, remove unidoc generated docs as well
  cleanFiles += (ScalaUnidoc / unidoc / target).value,
  // Scalac options, mirroring scaladoc settings
  ScalaUnidoc / unidoc / scalacOptions ++= Seq(
    "-sourcepath",
    (LocalRootProject / baseDirectory).value.getAbsolutePath,
    "-doc-source-url",
    scmInfo.value.get.browseUrl + "/tree/master€{FILE_PATH}.scala",
    "-doc-title",
    "Comet API - Docs",
    "-doc-version",
    version.value,
    "-doc-footer",
    "By ulitol97 - <3",
    "-skip-by-id:buildinfo",
    "-private",
    "-no-link-warnings"
  )
)

/* ------------------------------------------------------------------------- */
/* MDOC + DOCUSAURUS SETTINGS */
// Setup Mdoc + Docusaurus integration
// The docusaurus webpage will be located in /website
// See https://scalameta.org/mdoc/docs/docusaurus.html
// See https://docusaurus.io/

// Name of the directory inside the root folder containing the webdocs markdown
// For changes, edit docusaurus.config.js
lazy val docsFolder = "docs"
// Name of the directory inside the root folder containing the Docusaurus site
lazy val websiteFolder = "website"
lazy val mdocSettings = Seq(
  mdocVariables := Map(
    "APP_NAME" -> (Global / name).value.capitalize,
    "APP_INNER_NAME" -> (Global / name).value,
    "APP_VERSION" -> version.value,
    "WEBPAGE_URL" -> "https://ulitol97.github.io/comet/",
    "CLIENT_NAME" -> "RDFShape Client",
    "CLIENT_REPO" -> "https://github.com/weso/rdfshape-client/",
    "CLIENT_URL" -> "https://rdfshape.weso.es/",
    "WEBSITE_FOLDER" -> websiteFolder,
    "DOCS_FOLDER" -> docsFolder,
    // Reference the Scala versions from webdocs
    "SCALA_2_VERSION" -> scalaVersionPattern.replaceAllIn(scala2_13, ".x"),
    "SCALA_3_VERSION" -> scalaVersionPattern.replaceAllIn(scala3_1, ".x"),

    "API_URL" -> "https://api.rdfshape.weso.es",
    "API_CONTAINER_REGISTRY" -> "https://github.com/orgs/weso/packages/container/package/rdfshape-api",
    "WESOLOCAL_URL" -> "https://github.com/weso/wesolocal/wiki/RDFShape",
    "API-DOCS_URL" -> "https://app.swaggerhub.com/apis-docs/weso/RDFShape"
  ),
  // No warnings reported for dead links
  mdocExtraArguments := Seq("--no-link-hygiene"),
  // When creating/publishing the docusaurus site,
  // update the mdoc and scaladoc first
  docusaurusCreateSite := docusaurusCreateSite
    .dependsOn(Compile / unidoc)
    .value,
  docusaurusPublishGhpages :=
    docusaurusPublishGhpages
      .dependsOn(Compile / unidoc)
      .value
)

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