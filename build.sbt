val akkaVer = "2.5.4"
val logbackVer = "1.1.3"
val scalaVer = "2.12.3"
val scalaTestVer = "3.0.4"

lazy val dependencies = Seq(
  "com.typesafe.akka"        %% "akka-actor"                 % akkaVer,
  "com.typesafe.akka"        %% "akka-slf4j"                 % akkaVer,
  "ch.qos.logback"           %  "logback-classic"            % logbackVer,
  "com.typesafe.akka"        %% "akka-testkit"               % akkaVer            % "test",
  "org.scalatest"            %% "scalatest"                  % scalaTestVer       % "test"
)

name := "social-networking-kata"
organization := "com.social"
version := "1.0.0"
scalaVersion := scalaVer
parallelExecution in Test := false
logBuffered in Test := false
libraryDependencies ++= dependencies

