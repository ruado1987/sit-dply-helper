scalaHome := Some(file("C:/scala-2.10.1"))

scalacOptions ++= Seq("-feature", "-language:implicitConversions")

scalaVersion := "2.10.1"

libraryDependencies += "org.apache.commons" % "commons-vfs2" % "2.0"

libraryDependencies += "com.jcraft" % "jsch" % "0.1.49"

libraryDependencies += "org.scalatest" % "scalatest_2.10" % "1.9.1" % "test"

libraryDependencies += "org.mockito" % "mockito-all" % "1.9.5" % "test"

libraryDependencies += "org.apache.sshd" % "sshd-core" % "0.8.0"

testOptions in Test += Tests.Argument(TestFrameworks.ScalaTest, "-oDSF")

