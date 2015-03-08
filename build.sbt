lazy val commonSettings = Seq(
	organization := "com.davidthomasbernal",
	version := "0.1"
)

lazy val root = (project in file(".")).
	settings(commonSettings: _*).
	settings(
    name := "StarDict",
    libraryDependencies += "com.novocode" % "junit-interface" % "0.11" % "test",
    crossPaths := false
  )
