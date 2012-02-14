package test

import org.specs2.mutable._
import play.api.test._
import play.api.test.Helpers._
import play.api.mvc.Action
import play.api.mvc.AnyContent
import play.api.Logger

class ProblemsSpec extends Specification {
  
  import models._

  // -- Date helpers
  
  def dateIs(date: java.util.Date, str: String) = new java.text.SimpleDateFormat("yyyy-MM-dd").format(date) == str
  
  // --
  
  "Application" should {
    
    "show the problem list on  /" in {

      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
      
        val result : Action[AnyContent] = controllers.Problems.index()
        
        val actualResult = result.apply(FakeRequest())
        status(actualResult) must equalTo(OK)
      }
    }

     "show be able to handle + symbol  /" in {

      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
      
        val solution = """((n:String) => "Hello, " + n + "!")"""
        System.out.println("solution: " + solution)
        val result : Action[AnyContent] = controllers.Problems.solve()
        
        val request = FakeRequest().withFormUrlEncodedBody(("id", "8"), ("solution", solution))
        val actualResult = result.apply(request)
        contentAsString(actualResult) must contain("success")
        System.out.println(contentAsString(actualResult))
        status(actualResult) must equalTo(OK)
      }
    }

    "show that x does not equal to y" in{
    
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
      
        val solution = """false"""
        val result : Action[AnyContent] = controllers.Problems.solve()
        
        val actualResult = result.apply(FakeRequest().withFormUrlEncodedBody(("id","1"), ("solution",solution)) )
        contentAsString(actualResult) must contain("not equal [false == true]")
        System.out.println(contentAsString(actualResult))
        status(actualResult) must equalTo(OK)
      }
    }
  }
}
