name := "soul-kitchen"
version := "0.0.1"
scalaVersion := "2.13.1"

val catsVersion        = "2.1.0"
val catsEffectVersion  = "2.1.1"
val scalaTestVersion   = "3.1.0"
val mockitoVersion     = "1.11.2"
val http4sVersion      = "0.21.0"
val circeVersion       = "0.12.3"
val idGeneratorVersion = "1.2.1"
val logbackVersion     = "1.2.3"
val pureConfigVersion  = "0.12.2"

scalacOptions ++= Seq(
  "-encoding",
  "UTF-8",
  "-Xfatal-warnings",
  "-Xlint",
  "-feature",
  "-language:higherKinds",
  "-deprecation",
  "-unchecked"
)

libraryDependencies ++= Seq(
//  Cats
  "org.typelevel" %% "cats-core"   % catsVersion,
  "org.typelevel" %% "cats-effect" % catsEffectVersion,
//  Test
  "org.scalatest" %% "scalatest"               % scalaTestVersion % Test,
  "org.mockito"   %% "mockito-scala-scalatest" % mockitoVersion   % Test,
  "org.mockito"   %% "mockito-scala-cats"      % mockitoVersion   % Test,
//  Http4s
  "org.http4s" %% "http4s-dsl"          % http4sVersion,
  "org.http4s" %% "http4s-blaze-server" % http4sVersion,
  "org.http4s" %% "http4s-blaze-client" % http4sVersion,
  "org.http4s" %% "http4s-circe"        % http4sVersion,
//  Circe
  "io.circe" %% "circe-core"    % circeVersion,
  "io.circe" %% "circe-generic" % circeVersion,
  "io.circe" %% "circe-parser"  % circeVersion,
//  IdGenerator
//  "com.softwaremill.common" %% "id-generator" % idGeneratorVersion,
//  Logger
  "ch.qos.logback" % "logback-classic" % logbackVersion % Test,
//  PureConfig
  "com.github.pureconfig" %% "pureconfig"             % pureConfigVersion,
  "com.github.pureconfig" %% "pureconfig-cats-effect" % pureConfigVersion
)

addCompilerPlugin(
  "org.typelevel" %% "kind-projector" % "0.11.0" cross CrossVersion.full
)
