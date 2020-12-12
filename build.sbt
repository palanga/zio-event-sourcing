name := "zio-event-sourcing"
version := "0.0.0"

val mainScala = "2.13.4"
val allScala  = Seq(mainScala)

val zioVersion = "1.0.3"

inThisBuild(
  List(
    organization := "dev.palanga",
    homepage := Some(url("https://github.com/palanga/zio-event-sourcing")),
    licenses := List("Apache-2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0")),
    parallelExecution in Test := false,
    pgpPassphrase := sys.env.get("PGP_PASSWORD").map(_.toArray),
    pgpPublicRing := file("/tmp/public.asc"),
    pgpSecretRing := file("/tmp/secret.asc"),
    scmInfo := Some(
      ScmInfo(
        url("https://github.com/palanga/zio-event-sourcing/"),
        "scm:git:git@github.com:palanga/zio-event-sourcing.git",
      )
    ),
    developers := List(
      Developer(
        "palanga",
        "Andrés González",
        "a.gonzalez.terres@gmail.com",
        url("https://github.com/palanga"),
      )
    ),
  )
)

ThisBuild / publishTo := sonatypePublishToBundle.value

addCommandAlias("fmt", "all scalafmtSbt scalafmt test:scalafmt")
addCommandAlias("check", "all scalafmtSbtCheck scalafmtCheck test:scalafmtCheck")

lazy val root =
  project
    .in(file("."))
    .settings(skip in publish := true)
    .settings(historyPath := None)
    .aggregate(
      core,
      examples,
    )

lazy val core =
  project
    .in(file("core"))
    .settings(name := "zio-event-sourcing")
    .settings(commonSettings)
    .settings(
      testFrameworks := Seq(new TestFramework("zio.test.sbt.ZTestFramework")),
      libraryDependencies ++= Seq(
        "dev.zio" %% "zio"          % zioVersion,
        "dev.zio" %% "zio-streams"  % zioVersion,
        "dev.zio" %% "zio-test"     % zioVersion % "test",
        "dev.zio" %% "zio-test-sbt" % zioVersion % "test",
        compilerPlugin("com.olegpy" %% "better-monadic-for" % "0.3.1"),
      ),
    )
    .settings(
      fork in Test := true,
      fork in run := true,
    )

lazy val examples =
  project
    .in(file("examples"))
    .settings(name := "zio-event-sourcing-examples")
    .settings(commonSettings)
    .settings(
      libraryDependencies ++= Seq(compilerPlugin("com.olegpy" %% "better-monadic-for" % "0.3.1"))
    )
    .settings(
      fork in Test := true,
      fork in run := true,
      skip in publish := true,
    )
    .dependsOn(core)

val commonSettings =
  Def.settings(
    scalaVersion := mainScala,
    crossScalaVersions := allScala,
    scalacOptions ++= Seq(
      "-deprecation",
      "-encoding",
      "UTF-8",
      "-explaintypes",
      "-Yrangepos",
      "-feature",
      "-language:higherKinds",
      "-language:existentials",
      "-unchecked",
      "-Xlint:_,-type-parameter-shadow",
      //    "-Xfatal-warnings",
      "-Ywarn-numeric-widen",
      "-Ywarn-unused:patvars,-implicits",
      "-Ywarn-value-discard",
    ) ++ (CrossVersion.partialVersion(scalaVersion.value) match {
      case Some((2, 12)) =>
        Seq(
          "-Xsource:2.13",
          "-Yno-adapted-args",
          "-Ypartial-unification",
          "-Ywarn-extra-implicit",
          "-Ywarn-inaccessible",
          "-Ywarn-infer-any",
          "-Ywarn-nullary-override",
          "-Ywarn-nullary-unit",
          "-opt-inline-from:<source>",
          "-opt-warnings",
          "-opt:l:inline",
        )
      case _             => Nil
    }),
    scalacOptions in Test --= Seq("-Xfatal-warnings"),
  )