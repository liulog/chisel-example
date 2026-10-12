ThisBuild / scalaVersion := "2.13.18"
ThisBuild / organization := "example"
ThisBuild / version := "0.1.0-SNAPSHOT"

val chiselVersion = "7.16.0"

lazy val root = (project in file("."))
  .settings(
    name := "chisel-example",
    libraryDependencies += "org.chipsalliance" %% "chisel" % chiselVersion,
    addCompilerPlugin(
      ("org.chipsalliance" % "chisel-plugin" % chiselVersion).cross(CrossVersion.full)
    ),
    scalacOptions ++= Seq("-deprecation", "-feature", "-unchecked", "-language:reflectiveCalls"),
    Compile / run / mainClass := Some("example.GenerateVerilog"),
    Compile / run / fork := true
  )
