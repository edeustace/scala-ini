package test

import org.specs2.mutable._

import play.api.test._
import play.api.test.Helpers._

class ProblemSpec extends Specification {
  
  import models._

  // -- Date helpers
  
  def dateIs(date: java.util.Date, str: String) = new java.text.SimpleDateFormat("yyyy-MM-dd").format(date) == str
  
  // --
  
  "Problem model" should {
    
    "be retrieved by id" in {
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
        
        val trueIsTrue = Problem.findById(1)
        trueIsTrue.name must equalTo("True is true")
      }
    }
}    
  
}