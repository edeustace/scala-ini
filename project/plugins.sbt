// Comment to get more information during initialization
logLevel := Level.Warn

// The Typesafe repository contains all required dependencies
resolvers += "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"

resolvers += "Typesafe snapshot repository" at "http://repo.typesafe.com/typesafe/snapshots/"

// Use the Play sbt plugin for Play projects
addSbtPlugin("play" % "sbt-plugin" % "2.0-RC1-SNAPSHOT")