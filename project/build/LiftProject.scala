import sbt._

class LiftProject(info: ProjectInfo) extends DefaultWebProject(info)  {
  val liftVersion = "2.4-M2"

  // uncomment the following if you want to use the snapshot repo
    val scalatoolsSnapshot = ScalaToolsSnapshots

  // If you're using JRebel for Lift development, uncomment
  // this line
  override def scanDirectories = Nil
  
  // run directly from source dir
  override def jettyWebappPath  = webappPath

  override def libraryDependencies = Set(
    "net.liftweb" %% "lift-webkit" % liftVersion % "compile",
    "net.liftweb" %% "lift-mapper" % liftVersion % "compile",
    "net.liftweb" %% "lift-widgets" % liftVersion % "compile", 
    "net.liftweb" %% "lift-textile" % liftVersion,  
    "joda-time" % "joda-time" % "1.6", 
    "commons-io" % "commons-io" % "1.4",
    "postgresql" % "postgresql" % "9.0-801.jdbc4",
    "org.mortbay.jetty" % "jetty" % "6.1.22" % "test",
    "junit" % "junit" % "4.5" % "test",
    "ch.qos.logback" % "logback-classic" % "0.9.26",
    "org.scala-tools.testing" %% "specs" % "1.6.8" % "test",
    "com.h2database" % "h2" % "1.2.138"
  ) ++ super.libraryDependencies

}
