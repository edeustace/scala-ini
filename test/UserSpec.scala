package test

import org.specs2.mutable._

import play.api.test._
import play.api.test.Helpers._

class UserSpec extends Specification {
  
  import models._

  "User" should {
    
    /*"be retrieved by email" in {
      running(FakeApplication(additionalConfiguration = SpecHelper.testDb())) {
        
        User.findByEmail("ed.eustace@gmail.com") match {
          case Some(u) => u.email must equalTo("ed.eustace@gmail.com")
          case _ => throw new RuntimeException("can't find user")

        }
        
      }
    }*/

    "list users" in {

      running(FakeApplication(additionalConfiguration = SpecHelper.testDb())) {
        
        val result = User.findAll
        result.length must equalTo(2)
      }

    }
    
  }
  
}