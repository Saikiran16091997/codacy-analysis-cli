import sbt.Keys._
import sbt._

val scalaBinaryVersionNumber = "2.12"
val scalaVersionNumber = s"$scalaBinaryVersionNumber.10"

Global / useGpg := false

lazy val root = project
  .in(file("."))
  .settings(name := "root", Common.genericSettings)
  .aggregate(codacyAnalysisCore, codacyAnalysisCli)
  .settings(publish := {}, publishLocal := {}, publishArtifact := false)

lazy val codacyAnalysisCore = project
  .in(file("core"))
  .settings(name := "codacy-analysis-core")
  .settings(coverageExcludedPackages := "<empty>;com\\.codacy\\..*Error.*")
  .settings(Common.genericSettings)
  .settings(
    // App Dependencies
    libraryDependencies ++= Seq(
      Dependencies.caseApp,
      Dependencies.betterFiles,
      Dependencies.jodaTime,
      Dependencies.fansi,
      Dependencies.scalajHttp,
      Dependencies.jGit,
      Dependencies.cats) ++
      Dependencies.circe ++
      Dependencies.log4s ++
      Dependencies.codacyPlugins,
    // Test Dependencies
    libraryDependencies ++= Dependencies.specs2)
  .settings(
    // Sonatype repository settings
    publishMavenStyle := true,
    Test / publishArtifact := false,
    Docker / publish := {},
    Docker / publishLocal := {},
    pomIncludeRepository := (_ => false),
    publishTo := sonatypePublishToBundle.value)
  .settings(
    organizationName := "Codacy",
    organizationHomepage := Some(new URL("https://www.codacy.com")),
    startYear := Some(2018),
    description := "Library to analyse projects",
    licenses := Seq("AGPL-3.0" -> url("https://opensource.org/licenses/AGPL-3.0")),
    homepage := Some(url("https://github.com/codacy/codacy-analysis-cli")),
    pomExtra := <scm>
      <url>https://github.com/codacy/codacy-analysis-cli</url>
      <connection>scm:git:git@github.com:codacy/codacy-analysis-cli.git</connection>
      <developerConnection>scm:git:https://github.com/codacy/codacy-analysis-cli.git</developerConnection>
    </scm>
      <developers>
        <developer>
          <id>rtfpessoa</id>
          <name>Rodrigo Fernandes</name>
          <email>rodrigo [at] codacy.com</email>
          <url>https://github.com/rtfpessoa</url>
        </developer>
        <developer>
          <id>bmbferreira</id>
          <name>Bruno Ferreira</name>
          <email>bruno.ferreira [at] codacy.com</email>
          <url>https://github.com/bmbferreira</url>
        </developer>
        <developer>
          <id>xplosunn</id>
          <name>Hugo Sousa</name>
          <email>hugo [at] codacy.com</email>
          <url>https://github.com/xplosunn</url>
        </developer>
        <developer>
          <id>pedrocodacy</id>
          <name>Pedro Amaral</name>
          <email>pamaral [at] codacy.com</email>
          <url>https://github.com/pedrocodacy</url>
        </developer>
      </developers>)

lazy val codacyAnalysisCli = project
  .in(file("cli"))
  .settings(
    name := "codacy-analysis-cli",
    coverageExcludedPackages := "<empty>;com\\.codacy\\..*CLIError.*",
    Common.dockerSettings,
    Common.genericSettings,
    Universal / javaOptions ++= Seq("-XX:MinRAMPercentage=60.0", "-XX:MaxRAMPercentage=90.0"),
    publish := (Docker / publish).value,
    publishLocal := (Docker / publishLocal).value,
    publishArtifact := false,
    libraryDependencies ++= Dependencies.pprint +: Dependencies.specs2)
  .enablePlugins(JavaAppPackaging)
  .enablePlugins(DockerPlugin)
  .dependsOn(codacyAnalysisCore % "compile->compile;test->test")
  .aggregate(codacyAnalysisCore)

// Scapegoat
ThisBuild / scalaVersion := Common.scalaVersionNumber
ThisBuild / scalaBinaryVersion := Common.scalaBinaryVersionNumber
ThisBuild / scapegoatDisabledInspections := Seq()
ThisBuild / scapegoatVersion := "1.4.1"
