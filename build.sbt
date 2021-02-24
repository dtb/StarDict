lazy val commonSettings = Seq(
	organization := "com.davidthomasbernal",
	version := "0.1"
)

lazy val root = (project in file(".")).
	settings(commonSettings: _*).
	settings(
    name := "StarDict",
    libraryDependencies += "com.novocode" % "junit-interface" % "0.11" % "test",
    autoScalaLibrary := false,
    crossPaths := false,
    javacOptions in (Compile, compile) += "-g"
  )

scmInfo := Some(
  ScmInfo(
    url("https://github.com/indic-dict/StarDict-1"),
    "scm:git@github.com:indic-dict/StarDict-1.git"
  )
)

useGpg := true
publishMavenStyle := true
publishTo := Some(
  if (isSnapshot.value)
    Opts.resolver.sonatypeSnapshots
  else
    Opts.resolver.sonatypeStaging
)

import ReleaseTransformations._

releaseProcess := Seq[ReleaseStep](
  checkSnapshotDependencies,
  inquireVersions,
  runClean,
  runTest,
  setReleaseVersion,
  commitReleaseVersion,
  tagRelease,
  releaseStepCommand("publishSigned"),
  setNextVersion,
  commitNextVersion,
  releaseStepCommand("sonatypeReleaseAll"),
  pushChanges
)