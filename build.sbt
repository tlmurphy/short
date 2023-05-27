ThisBuild / organization := "com.tlmurphy"

// Akka dependencies
lazy val akkaHttpVersion = "10.2.9"
lazy val akkaVersion = "2.6.19"

// Http4s Dependencies
val Http4sVersion = "0.23.6"
val LogbackVersion = "1.2.6"

lazy val shortAkkaHttp = (project in file("short-akka-http"))
  .settings(
    name := "short-akka-http",
    scalaVersion := "2.13.4",
    version := "1.0",
    fork := true,
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-http" % akkaHttpVersion,
      "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpVersion,
      "com.typesafe.akka" %% "akka-actor-typed" % akkaVersion,
      "com.typesafe.akka" %% "akka-stream" % akkaVersion,
      "ch.qos.logback" % "logback-classic" % "1.2.3",
      "ch.megard" %% "akka-http-cors" % "1.1.3",
      "com.typesafe.akka" %% "akka-http-testkit" % akkaHttpVersion % Test,
      "com.typesafe.akka" %% "akka-actor-testkit-typed" % akkaVersion % Test,
      "org.scalatest" %% "scalatest" % "3.1.4" % Test
    )
  )

lazy val shortHttp4s = (project in file("short-http4s"))
  .settings(
    name := "short-http4s",
    scalaVersion := "3.2.2",
    version := "0.1",
    libraryDependencies ++= Seq(
      "org.http4s" %% "http4s-ember-server" % Http4sVersion,
      "org.http4s" %% "http4s-ember-client" % Http4sVersion,
      "org.http4s" %% "http4s-circe" % Http4sVersion,
      "io.circe" %% "circe-generic" % "0.14.1",
      "org.http4s" %% "http4s-dsl" % Http4sVersion,
      "org.typelevel" %% "munit-cats-effect" % "2.0.0-M3" % Test,
      "ch.qos.logback" % "logback-classic" % LogbackVersion
    ),
    testFrameworks += new TestFramework("munit.Framework")
  )

lazy val shortPlay = (project in file("short-play"))
  .enablePlugins(PlayScala)
  .settings(
    name := "short-play",
    scalaVersion := "2.13.10",
    version := "0.1",
    libraryDependencies ++= Seq(
      guice,
      "org.scalatestplus.play" %% "scalatestplus-play" % "5.0.0" % Test
    )
  )
