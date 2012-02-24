package test

import org.specs2.mutable._
import play.api.test._
import play.api.test.Helpers._
import play.api.mvc.Action
import play.api.mvc.AnyContent
import play.api.Logger


class ProblemsSpec extends Specification {
  
  import models._
  import controllers._

  val beginTag = Problems.PuzzleRegex.BEGIN 
  val endTag = Problems.PuzzleRegex.END


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
        contentAsString(actualResult) must contain(controllers.Problems.EXPECTED_TRUE_GOT_FALSE)
        System.out.println(contentAsString(actualResult))
        status(actualResult) must equalTo(OK)
      }
    }

    "solveAndSubmit should return invalid syntax for puzzle none" in{
      val solution = """true == true"""
      assertSolveResponse( solution,controllers.Problems.INVALID_SOLUTION_SYNTAX )
    }

    "solveAndSubmit should return invalid syntax for puzzle no end" in{
      val solution = beginTag + "true == true"
      assertSolveResponse( solution,controllers.Problems.INVALID_SOLUTION_SYNTAX )
    }

    "solveAndSubmit should return invalid syntax for puzzle no begin" in{
      val solution = "true" + endTag + "== true"
      assertSolveResponse( solution,controllers.Problems.INVALID_SOLUTION_SYNTAX )
    }

    def assertSolveResponse( solution : String, response : String ) = {
      
      
      
      

      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
      
        /*
        //TODO: How to login during testing
        val signupAction : Action[AnyContent] = controllers.Application.completeSignup()

        val signupResult = signupAction.apply(
          FakeRequest().withFormUrlEncodedBody(
            ("email", "ed.eustace@gmail.com"),
            ("password", "password")
            )
        )
        */
        val solveAction : Action[AnyContent] = controllers.Problems.solveAndSubmit()
        
        val args : Array[Tuple2[String,String]] = Array(
          (Problems.Params.SOLUTION, solution),
          (Problems.Params.NAME, "test puzzle"),
          (Problems.Params.DESCRIPTION, "test description"),
          (Problems.Params.LEVEL, "test level"),
          (Problems.Params.CATEGORY, "test category")
        )

        val solveResult = 
          solveAction.apply(
            FakeRequest().withFormUrlEncodedBody( args: _* ) 
          )
          
        contentAsString(solveResult) must contain(response)
        System.out.println(contentAsString(solveResult))
        status(solveResult) must equalTo(OK)
      }
    }
    
    "solveAndSubmit should return solved for puzzle with valid syntax" in{
      val solution = beginTag + "true" + endTag + "== true"
      assertSolveResponse( solution,controllers.Problems.SUBMITTED )
    }
   
  }
}
