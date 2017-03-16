name := "timer"

version := "1.0"

lazy val `timer` = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.7"

libraryDependencies ++= Seq( jdbc , cache , ws   , specs2 % Test )

unmanagedResourceDirectories in Test <+=  baseDirectory ( _ /"target/web/public/test" )  

resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases"

resolvers += "krasserm at bintray" at "http://dl.bintray.com/krasserm/maven"

libraryDependencies ++= Seq(
  "com.github.krasserm" %% "akka-persistence-cassandra-3x" % "0.6",
  "com.typesafe.akka" %% "akka-cluster-sharding" % "2.4.17",
  "com.typesafe.akka" %% "akka-cluster" % "2.4.17",
  "com.typesafe.akka" %% "akka-remote" % "2.4.17",
  "com.typesafe.akka" %% "akka-stream" % "2.4.17",
  "com.typesafe.play" %% "play-slick" % "2.0.0",
  "org.postgresql" % "postgresql" % "9.4.1212",
  "com.typesafe.akka" %% "akka-persistence-query-experimental" % "2.4.17"
)







