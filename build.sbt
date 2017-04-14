name := """hello-scala"""

version := "1.0"

scalaVersion := "2.12.1"

lazy val camelVersion = "2.18.3"

libraryDependencies ++=Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.4.17",
"com.typesafe.akka" %% "akka-agent" % "2.4.17",
"com.typesafe.akka" %% "akka-camel" % "2.4.17",
"com.typesafe.akka" %% "akka-cluster" % "2.4.17",
"com.typesafe.akka" %% "akka-cluster-metrics" % "2.4.17",
"com.typesafe.akka" %% "akka-cluster-sharding" % "2.4.17",
"com.typesafe.akka" %% "akka-cluster-tools" % "2.4.17",
"com.typesafe.akka" %% "akka-contrib" % "2.4.17",
"com.typesafe.akka" %% "akka-multi-node-testkit" % "2.4.17",
"com.typesafe.akka" %% "akka-osgi" % "2.4.17",
"com.typesafe.akka" %% "akka-persistence" % "2.4.17",
"com.typesafe.akka" %% "akka-persistence-tck" % "2.4.17",
"com.typesafe.akka" %% "akka-remote" % "2.4.17",
"com.typesafe.akka" %% "akka-slf4j" % "2.4.17",
"com.typesafe.akka" %% "akka-stream" % "2.4.17",
"com.typesafe.akka" %% "akka-stream-testkit" % "2.4.17",
"com.typesafe.akka" %% "akka-testkit" % "2.4.17",
"com.typesafe.akka" %% "akka-distributed-data-experimental" % "2.4.17",
"com.typesafe.akka" %% "akka-typed-experimental" % "2.4.17",
"com.typesafe.akka" %% "akka-persistence-query-experimental" % "2.4.17",
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


