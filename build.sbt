import scalariform.formatter.preferences._

scalacOptions ++= Seq("-feature", "-language:implicitConversions", "-deprecation")

scalaVersion := "2.10.1"

libraryDependencies += "com.github.scala-incubator.io" %% "scala-io-core" % "0.4.2"

libraryDependencies += "com.github.scala-incubator.io" %% "scala-io-file" % "0.4.2"

libraryDependencies += "net.noerd" %% "prequel" % "0.3.8"

libraryDependencies += "org.apache.commons" % "commons-vfs2" % "2.0"

libraryDependencies += "com.jcraft" % "jsch" % "0.1.49"

libraryDependencies += "org.apache.poi" % "poi" % "3.9"

libraryDependencies += "org.scalatest" % "scalatest_2.10" % "1.9.1" % "test"

libraryDependencies += "org.mockito" % "mockito-all" % "1.9.5" % "test"

libraryDependencies += "org.apache.sshd" % "sshd-core" % "0.8.0" % "test"

libraryDependencies += "com.h2database" % "h2" % "1.3.171" % "test"

libraryDependencies += "org.slf4j" % "slf4j-jdk14" % "1.7.5"

testOptions in Test += Tests.Argument(TestFrameworks.ScalaTest, "-oDSF")

defaultScalariformSettings

ScalariformKeys.preferences := FormattingPreferences()
  .setPreference(SpaceInsideBrackets, true)
  .setPreference(SpaceInsideParentheses, true)
  .setPreference(AlignParameters, true)
  .setPreference(CompactStringConcatenation, true)
  .setPreference(SpaceBeforeColon, true)
