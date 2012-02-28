package test

import org.specs2.mutable._
import play.api.test._
import play.api.test.Helpers._
import play.api.mvc.Action
import play.api.mvc.AnyContent
import play.api.Logger


class PuzzleEvaluatorSpec extends Specification {
  
  import com.ee._
  import controllers._

  
  def resultMessage(successful:Int = 0,failed:Int=0) : String = PuzzleEvaluator.getSummary(successful, failed)
  
  "PuzzleEvaluator" should {

    "return a compilation exception" in {
      val result = PuzzleEvaluator.solve("asdfads d= 238.0230.32498 afsdf adsasdf")
      result.summary must equalTo( PuzzleEvaluator.CompilationException)
    }


    "return a null or empty string error" in {
      val result = PuzzleEvaluator.solve(null)
      result.summary must equalTo( PuzzleEvaluator.Error.NULL_STRING)
    }

    "return a null or empty string error" in {
      val result = PuzzleEvaluator.solve("")
      result.summary must equalTo( PuzzleEvaluator.Error.EMPTY_STRING)
    }

    "fail on a single false" in {
        val result = PuzzleEvaluator.solve("""false""")
        result.summary must equalTo(resultMessage(0,1))
    }

    "pass on a simple single evaluation" in {
        val result = PuzzleEvaluator.solve("""def plusTwo(x:Int) : Int = {
          x + 2
        }

        plusTwo(1) == 3""")
        result.summary must equalTo(resultMessage(1))

        val firstSuccess = result.evaluations.filter(_.successful).head
        firstSuccess.line must equalTo(4)
    }
  
    "pass on multiple evaluations" in {
        val result = PuzzleEvaluator.solve("""def plusTwo(x:Int) : Int = {
          x + 2
        }

        plusTwo(1) == 3
        plusTwo(3) == 5""")
        result.summary must equalTo(resultMessage(2))
    }

    "return mixed results when some evaluations fail" in {
        val result = PuzzleEvaluator.solve("""def plusTwo(x:Int) : Int = {
          x + 2
        }

        plusTwo(1) == 3
        plusTwo(3) == 5
        plusTwo(5) == 5""")
        result.summary must equalTo(resultMessage(2,1))

        val firstFailure = result.evaluations.filter(!_.successful).head

        firstFailure.line must equalTo(6)

        val firstSuccess = result.evaluations.filter(_.successful).head
        firstSuccess.line must equalTo(4)
    }
    /*
    "evaluate correctly when the solution contains ==" in {



        isZero(1) == false 
        isZero(0) == true"""

      val clean = TagRegex.replaceAllIn(str,"")
      println("clean")
      println(clean)
      val solutionRegex = WholeTagRegex.replaceAllIn(str, "(.*?)" )
      println("solutinRegex") 
      println(solutionRegex) 
      val result = PuzzleEvaluator.solve(clean)
      result.summary must equalTo(resultMessage(2))
    }
  }*/
}
   
}
