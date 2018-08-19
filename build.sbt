name := """hello-scala"""

version := "1.0"

scalaVersion := "2.12.2"

lazy val camelVersion = "2.15.6"

lazy val akkaVersion = "2.5.3"

//resolvers += Resolver.sonatypeRepo("public")


libraryDependencies ++=Seq(
    "com.typesafe.akka" %% "akka-actor" % akkaVersion,
  "com.typesafe.akka" %% "akka-agent" % akkaVersion,
  "com.typesafe.akka" %% "akka-camel" % akkaVersion,
  "com.typesafe.akka" %% "akka-cluster" % akkaVersion,
  "com.typesafe.akka" %% "akka-cluster-metrics" % akkaVersion,
  "com.typesafe.akka" %% "akka-cluster-sharding" % akkaVersion,
  "com.typesafe.akka" %% "akka-cluster-tools" % akkaVersion,
    "com.typesafe.akka" %% "akka-distributed-data"  % akkaVersion,
  "com.typesafe.akka" %% "akka-multi-node-testkit" % akkaVersion,
  "com.typesafe.akka" %% "akka-osgi" % akkaVersion,
  "com.typesafe.akka" %% "akka-persistence" % akkaVersion,
  "com.typesafe.akka" %% "akka-persistence-query" % akkaVersion,
  "com.typesafe.akka" %% "akka-persistence-tck" % akkaVersion,
  "com.typesafe.akka" %% "akka-remote" % akkaVersion,
  "com.typesafe.akka" %% "akka-slf4j" % akkaVersion,
  "com.typesafe.akka" %% "akka-stream" % akkaVersion,
  "com.typesafe.akka" %% "akka-stream-testkit" % akkaVersion,
  "com.typesafe.akka" %% "akka-testkit" % akkaVersion,
    "com.typesafe.akka" %% "akka-typed" % akkaVersion,
    "com.typesafe.akka" %% "akka-contrib" % akkaVersion,
    "com.typesafe.akka" %% "akka-http" % "10.0.5",
    "com.typesafe.akka" %% "akka-http-testkit" % "10.0.5",
    "com.typesafe.akka" %% "akka-http-spray-json" % "10.0.5",
"org.scalanlp" %% "breeze" % "0.13.2",

// It also packages various blas implementations, which have licenses that may or may not
// be compatible with the Apache License. No GPL code, as best I know.
"org.scalanlp" %% "breeze-natives" % "0.13.2",

// The visualization library is distributed separately as well.
// It depends on LGPL code
"org.scalanlp" %% "breeze-viz" % "0.13.2"
).map(_.excludeAll(ExclusionRule("commons-logging"),ExclusionRule("org.scala-lang"),ExclusionRule("org.scala-lang.module")).exclude("log4j","log4j"))

libraryDependencies += "org.scala-lang" % "scala-library" % scalaVersion.value
libraryDependencies += "org.scala-lang" % "scala-reflect" % scalaVersion.value
libraryDependencies += "org.scala-lang.modules" %% "scala-xml" % "1.0.6"

// https://mvnrepository.com/artifact/org.apache.camel/camel-core
libraryDependencies ++= Seq(
  "org.apache.camel" % "camel-core" % camelVersion
)


// https://mvnrepository.com/artifact/org.slf4j/slf4j-api
libraryDependencies += "org.slf4j" % "slf4j-api" % "1.7.23"
// https://mvnrepository.com/artifact/org.slf4j/slf4j-nop
//libraryDependencies += "org.slf4j" % "slf4j-nop" % "1.7.23"

libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.2.3"

// https://mvnrepository.com/artifact/com.rabbitmq/amqp-client
libraryDependencies += ("com.rabbitmq" % "amqp-client" % "4.0.2").exclude("log4j","log4j")
  .exclude("org.slf4j","slf4j-log4j12")

// hadoop的lib
libraryDependencies += ("org.apache.hadoop" % "hadoop-common" % "2.8.1")
  .exclude("org.codehaus.jackson","jackson-core-asl")
  .exclude("org.codehaus.jackson","jackson-mapper-asl")
  .exclude("commons-codec","commons-codec")
  .exclude("commons-logging","commons-logging")
  .exclude("io.netty","netty")
  .exclude("log4j","log4j")
  .exclude("org.slf4j","slf4j-log4j12")
libraryDependencies += ("joda-time" % "joda-time" % "2.9.9").exclude("log4j","log4j")
  .exclude("org.slf4j","slf4j-log4j12")
libraryDependencies += ("jline" % "jline" % "2.14.3").exclude("log4j","log4j")
  .exclude("org.slf4j","slf4j-log4j12")

libraryDependencies += "org.apache.hbase" % "hbase-client" % "2.0.0"


//libraryDependencies += "org.scala-lang.modules" %% "scala-xml" % "1.0.6"
//
//libraryDependencies += "org.scala-lang" % "scala-reflect" % "2.12.1"

//libraryDependencies += "org.codehaus.jackson" % "jackson-core-asl" % "1.9.13"
//libraryDependencies += "org.codehaus.jackson" % "jackson-mapper-asl" % "1.9.13"

libraryDependencies += ("org.jsoup" % "jsoup" % "1.10.3").exclude("log4j","log4j")
  .exclude("org.slf4j","slf4j-log4j12")

// https://mvnrepository.com/artifact/org.apache.zookeeper/zookeeper
libraryDependencies += "org.apache.zookeeper" % "zookeeper" % "3.4.10" pomOnly()
resolvers += "Sonatype Releases" at "https://oss.sonatype.org/content/repositories/releases/"

//fork in run := true
//
//cancelable in Global := true
//
//assemblySettings
//
//
//import AssemblyKeys._
//
//lazy val buildSettings = Seq(
//  version := "0.1-SNAPSHOT",
//  organization := "lew.bing.hello-scala",
//  scalaVersion := "2.10.1"
//)
//
//val app = (project in file("hello-scala")).
//  settings(buildSettings: _*).
//  settings(assemblySettings: _*)

