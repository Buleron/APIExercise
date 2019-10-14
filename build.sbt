name := """exercise"""
organization := "com.example"


version := "3.0.3"
val akkaVersion = "2.5.22"

scalaVersion := "2.12.8"
//libraryDependencies += "com.auth0" % "java-jwt" % "3.3.0"
libraryDependencies ++= Seq(
  guice,
  ws,
  "org.webjars" % "swagger-ui" % "2.2.0",
  "org.projectlombok" % "lombok" % "1.16.20",
  "org.hibernate" % "hibernate-validator" % "6.0.17.Final",
  "org.mongodb" % "mongo-java-driver" % "3.6.4",
  // akka related stuff
  "com.typesafe.akka" %% "akka-actor" % akkaVersion,
  "com.typesafe.akka" %% "akka-stream" % akkaVersion,
  "com.typesafe.akka" %% "akka-cluster" % akkaVersion,
  "com.typesafe.akka" %% "akka-discovery" % akkaVersion,
  "com.typesafe.akka" %% "akka-slf4j" % akkaVersion,
  "com.typesafe.akka" %% "akka-cluster-tools" % akkaVersion,
)
resolvers += Resolver.url("Typesafe Ivy releases", url("https://repo.typesafe.com/typesafe/ivy-releases"))(Resolver.ivyStylePatterns)

//lazy val models = project.in(file("models")).enablePlugins(PlayScala)
lazy val root = project.in(file(".")).enablePlugins(PlayScala)
////.dependsOn(models).aggregate(models)
//lazy val root = (project in file("."))
//  .enablePlugins(PlayScala, PlayNettyServer)
//  .disablePlugins(PlayAkkaHttpServer)