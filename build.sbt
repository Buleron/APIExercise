name := """exercise"""
organization := "com.example"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.12.8"
libraryDependencies += guice
libraryDependencies += ws
libraryDependencies += "com.auth0" % "java-jwt" % "3.3.0"

libraryDependencies ++= Seq(
  "org.projectlombok" % "lombok" % "1.16.20",
  "org.hibernate" % "hibernate-validator" % "6.0.17.Final",
  "org.mongodb.morphia" % "morphia" % "1.2.1",
)
resolvers += Resolver.url("Typesafe Ivy releases", url("https://repo.typesafe.com/typesafe/ivy-releases"))(Resolver.ivyStylePatterns)