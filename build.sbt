name := """skiing-singapore"""

version := "1.0"

scalaVersion := "2.11.6"

resolvers += "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases"

scalacOptions in Test ++= Seq("-Yrangepos")

libraryDependencies ++= Seq(
  "org.specs2" %% "specs2-core" % "3.5" % "test",
  "net.databinder.dispatch" %% "dispatch-core" % "0.11.2",
  "ch.qos.logback" % "logback-classic" % "1.1.3"
)

// Uncomment to use Akka
//libraryDependencies += "com.typesafe.akka" % "akka-actor_2.11" % "2.3.9"


