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
        trueIsTrue.user_email must equalTo("ed.eustace@gmail.com")
      }
    }
  } 

  "ProbleMasker" should {

    "replace all tags" in {

      ProblemMasker.mask("/*<*/hello/*>*/") must equalTo("?")


      val masked = ProblemMasker.mask("""/*<*/hello/*>*/ 
        /*<*/hel
        lo/*>*/ 
        /*<*/hello/*>*/""") 

      println("masked: [" + masked + "]")
      masked.count(_ == '?' ) must equalTo(3)
    }
  }   
  
}