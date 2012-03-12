import sbt._
import Keys._
import PlayProject._

object ApplicationBuild extends Build {

    val appName         = "scala-ini"
    val appVersion      = "1.0"

    val appDependencies = Seq(
      // Add your project dependencies here,
      "postgresql" % "postgresql" % "9.1-901.jdbc4",
      "org.scala-lang" % "scala-compiler" % "2.9.1"

    )

    val main = PlayProject(appName, appVersion, appDependencies, mainLang = SCALA).settings(
      // Add your own project settings here      
    )

}
