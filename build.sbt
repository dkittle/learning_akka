name := "learning_akka"

version := "1.0"

scalaVersion := "2.12.3"

libraryDependencies ++= {

  val akkaVersion = "2.4.16"
  val akkaHttpVersion = "10.0.1"

  Seq(
    "com.typesafe.akka" %% "akka-actor" % akkaVersion,
    "com.typesafe.akka" %% "akka-http" % akkaHttpVersion,
    "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpVersion,
    "com.syncthemall" % "boilerpipe" % "1.2.2",
    "org.scala-lang.modules" %% "scala-xml" % "1.0.6",
    "com.typesafe.akka" %% "akka-testkit" % akkaVersion % "test",
    "com.typesafe.akka" %% "akka-http-testkit" % akkaHttpVersion % Test,
    "org.scalatest" %% "scalatest" % "3.0.1" % "test"
  )
}
