package test;

object SpecHelper{
	

	def testDb() : Map[String,String] = {
    Map(
        "db.default.driver" -> "org.postgresql.Driver",
        "db.default.url" -> "postgres://scalaini:hello@localhost/scalaini_test"
      )

  }
}