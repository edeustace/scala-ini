package test

import org.specs2.mutable._

import play.api.test._
import play.api.test.Helpers._

class ModelSpec extends Specification {
  
  import models._

  // -- Date helpers
  
  def dateIs(date: java.util.Date, str: String) = new java.text.SimpleDateFormat("yyyy-MM-dd").format(date) == str
  
  // --
  
  "Problem" should {
    
    "be retrieved by id" in {
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
        
        val prob = Problem.findById(1)
        //println("found prob: " + prob) 
        prob.name must equalTo("True is true")
        prob.user_email must equalTo("ed.eustace@gmail.com")
        prob.user_name must equalTo("ed eustace")
        
      }
    }
    
  }
  
}