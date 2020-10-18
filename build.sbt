name := "forex"
version := "1.0.0"

scalaVersion := "2.12.12"
scalacOptions ++= Seq(
  "-deprecation",
  "-encoding",
  "UTF-8",
  "-feature",
  "-language:existentials",
  "-language:higherKinds",
  "-Ypartial-unification",
  "-language:experimental.macros",
  "-language:implicitConversions"
)

resolvers +=
  "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"

val akkaHttpVersion = "10.1.12"
val akkaVersion = "2.6.6"

libraryDependencies ++= Seq(
  "com.github.pureconfig"      %% "pureconfig"           % "0.13.0",
  "com.softwaremill.quicklens" %% "quicklens"            % "1.6.1",
  "com.typesafe.akka"          %% "akka-stream"          % akkaVersion,
  "com.typesafe.akka"          %% "akka-http"            % akkaHttpVersion,
  "de.heikoseeberger"          %% "akka-http-circe"      % "1.33.0",
  "io.circe"                   %% "circe-core"           % "0.13.0",
  "io.circe"                   %% "circe-generic"        % "0.13.0",
  "io.circe"                   %% "circe-generic-extras" % "0.13.0",
  "io.circe"                   %% "circe-jawn"           % "0.13.0",
  "org.atnos"                  %% "eff"                  % "5.10.0",
  "org.atnos"                  %% "eff-monix"            % "5.10.0",
  "org.zalando"                %% "grafter"              % "2.6.1",
  "ch.qos.logback"             % "logback-classic"       % "1.2.3",
  "com.typesafe.scala-logging" %% "scala-logging"        % "3.9.2",
  "com.github.blemale"         % "scaffeine_2.12"        % "4.0.2",
  "org.scalatest"              %% "scalatest"            % "3.2.0" % "test",
  "org.scalatest"              %% "scalatest-funsuite"   % "3.2.0" % "test",
  "com.typesafe.akka"          %% "akka-stream-testkit"  % akkaVersion % "test",
  "com.typesafe.akka"          %% "akka-http-testkit"    % akkaHttpVersion % "test"
)

addCompilerPlugin("org.typelevel"   %% "kind-projector" % "0.11.0" cross CrossVersion.full)
addCompilerPlugin("org.scalamacros" %% "paradise"       % "2.1.1" cross CrossVersion.full)
