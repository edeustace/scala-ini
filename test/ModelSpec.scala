package test

import org.specs2.mutable._

import play.api.test._
import play.api.test.Helpers._

class ModelSpec extends Specification {
  
  import models._

  "User" should {
    
    "be retrieved by email" in {
      running(FakeApplication(additionalConfiguration = SpecHelper.testDb())) {
        
        val user = User.findByEmail("ed.eustace@gmail.com")
        user.email must equalTo("ed.eustace@gmail.com")
        
      }
    }
    
  }
  
}