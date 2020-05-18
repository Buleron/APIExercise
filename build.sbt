name := """exercise"""
//organization := "com.example"

version := "2.7.x"

scalaVersion := "2.12.8"
//version := "3.0.3"

resolvers += Resolver.bintrayIvyRepo("sohoffice", "sbt-plugins")

lazy val root = project.in(file(".")).enablePlugins(PlayScala)
  .enablePlugins(PlayScala, SwaggerPlugin)
  .settings(
    // Make sure you set the swaggerDomainNameSpaces according to your package structure.
    // You'll need this setting, otherwise swagger will fail.
    //
     swaggerDomainNameSpaces := Seq("io")
  )
val akkaVersion = "2.5.22"
val akkaManagementVersion = "1.0.0"
val akkaHTTPVersion = "10.1.10"

libraryDependencies += "org.webjars" %% "webjars-play" % "2.7.3"
libraryDependencies += "org.webjars" % "flot" % "0.8.3"
libraryDependencies += "org.webjars" % "bootstrap" % "3.3.6"
libraryDependencies += "com.auth0" % "java-jwt" % "3.3.0"
libraryDependencies ++= Seq(
  guice,
  ws,
  ehcache,
  "org.webjars" % "swagger-ui" % "2.2.0",
  "org.projectlombok" % "lombok" % "1.16.20",
  "org.hibernate" % "hibernate-validator" % "6.0.17.Final",
  "org.mongodb" % "mongo-java-driver" % "3.6.4",
  "com.nulab-inc" %% "scala-oauth2-core" % "1.3.0",
  "com.nulab-inc" %% "play2-oauth2-provider" % "1.3.0",

  "com.google.code.gson" % "gson" % "2.8.2",
  "org.reflections" % "reflections" % "0.9.11",
  "de.flapdoodle.embed" % "de.flapdoodle.embed.mongo" % "2.0.0",
  "org.glassfish" % "javax.el" % "3.0.0",
  "org.hibernate" % "hibernate-validator" % "6.0.17.Final",

  "org.mindrot" % "jbcrypt" % "0.3m",
  // akka related stuff
  "com.typesafe.akka" %% "akka-actor" % akkaVersion,
  "com.typesafe.akka" %% "akka-stream" % akkaVersion,
  "com.typesafe.akka" %% "akka-cluster" % akkaVersion,
  "com.typesafe.akka" %% "akka-discovery" % akkaVersion,
  "com.typesafe.akka" %% "akka-slf4j" % akkaVersion,
  "com.typesafe.akka" %% "akka-cluster-tools" % akkaVersion,
  // akka cluster related stuff
  "com.lightbend.akka.discovery" %% "akka-discovery-kubernetes-api" % akkaManagementVersion,
  "com.lightbend.akka.management" %% "akka-management" % akkaManagementVersion,
  "com.lightbend.akka.management" %% "akka-management-cluster-http" % akkaManagementVersion,
  "com.lightbend.akka.management" %% "akka-management-cluster-bootstrap" % akkaManagementVersion,

  "com.typesafe.akka" %% "akka-http" % akkaHTTPVersion
)

resolvers += "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases"
resolvers += Resolver.url("Typesafe Ivy releases", url("https://repo.typesafe.com/typesafe/ivy-releases"))(Resolver.ivyStylePatterns)
resolvers += Resolver.typesafeRepo("releases")
resolvers += Resolver.sbtPluginRepo("releases")
resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"
