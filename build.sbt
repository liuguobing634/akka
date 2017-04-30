name := """hello-scala"""

version := "1.0"

scalaVersion := "2.12.1"

lazy val camelVersion = "2.18.3"

lazy val akkaVersion = "2.5.0"

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
"org.scala-lang" % "scala-library" % scalaVersion.value
)

// https://mvnrepository.com/artifact/org.apache.camel/camel-core
libraryDependencies += "org.apache.camel" % "camel-core" % camelVersion

libraryDependencies += "org.apache.camel" % "camel-http" % camelVersion
// https://mvnrepository.com/artifact/org.apache.camel/camel-scala
libraryDependencies += "org.apache.camel" % "camel-scala" % camelVersion

// https://mvnrepository.com/artifact/org.slf4j/slf4j-api
libraryDependencies += "org.slf4j" % "slf4j-api" % "1.7.16"
// https://mvnrepository.com/artifact/org.slf4j/slf4j-nop
libraryDependencies += "org.slf4j" % "slf4j-nop" % "1.7.16"

// https://mvnrepository.com/artifact/com.rabbitmq/amqp-client
libraryDependencies += "com.rabbitmq" % "amqp-client" % "4.0.2"



fork in run := true

cancelable in Global := true


