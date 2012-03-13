package test;

object SpecHelper{
	

	def testDb() : Map[String,String] = {
    Map(
        "db.default.driver" -> "org.postgresql.Driver",
        "db.default.url" -> "jdbc:postgresql://localhost/scalaini_test",
        "db.default.user" -> "scalaini",
		"db.default.password" -> "hello"
      )

  }
}