name := "sparkscala"

version := "1.0"

//scalaVersion := "2.11.4"
scalaVersion := "2.11.8"

libraryDependencies ++= {
//  val sparkVer = "2.1.0"
  Seq(
    "org.apache.spark" %% "spark-core" % "2.3.0",
    "org.apache.logging.log4j" % "log4j-api" % "2.10.0",
    "org.apache.logging.log4j" % "log4j-core" % "2.10.0"
  )
}
