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
      val result = PuzzleEvaluator.solve("asdfads")
      val containsSpecificError = result.summary.contains("not found: value asdfads")
      val containsGeneralError = result.summary.contains(PuzzleEvaluator.CompilationException)
      containsSpecificError must equalTo(true)
      containsGeneralError must equalTo(true)
    }


    "return a compilation exception" in {
      val result = PuzzleEvaluator.solve("""//booleans
true == ?asdf
false == ?""")
      println("result.summary")
      println(result.summary)
      val containsSpecificError = result.summary.contains("not found: value ?")
      containsSpecificError must equalTo(true)
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


  "PreparedPuzzleString" should {
    "prepare it correctly" in {
      val s = "x == y"

      val eval = PreparedPuzzleString.SINGLE_EVAL
        .replace(PreparedPuzzleString.TAGS.INDEX, "0")
        .replace(PreparedPuzzleString.TAGS.BOOLEAN, "x == y")
      
      val expected = PreparedPuzzleString.TEMPLATE
        .replace(PreparedPuzzleString.TAGS.LINES, eval)

      println("expected: ")
      println(expected)
      (new PreparedPuzzleString)(s) must equalTo( expected )
    }


    "work with multiline" in {
        val s = """def addOne(x:Int) : Int = x + 1
addOne(1) == 2
addOne(2) == 3"""

        val expected = """//declare the list
var __evalOut__ : List[Tuple2[Int,Boolean]] = List()
def addOne(x:Int) : Int = x + 1
__evalOut__ = __evalOut__ ::: List((1, addOne(1) == 2))
__evalOut__ = __evalOut__ ::: List((2, addOne(2) == 3))
__evalOut__"""

    (new PreparedPuzzleString)(s) must equalTo(expected)
    
    }
  }


  "work for a real example" in {
    val s = """"hello world" .toUpperCase == "HELLO WORLD"

"HELLO WORLD" .toLowerCase == "hello world"

// fill in this method so that a string of words gets capitalized
def capitalize(s: String) = {

  def capitalizeWord( w : String ) = w(0).toUpper + w.substring(1, w.length).toLowerCase
  val wordList = s.split("\\s").toList.map( capitalizeWord )
  wordList.reduceLeft(_ + " " + _)

}

capitalize("man OF stEEL") == "Man Of Steel" """

  val expected = """//declare the list
var __evalOut__ : List[Tuple2[Int,Boolean]] = List()
__evalOut__ = __evalOut__ ::: List((0, "hello world" .toUpperCase == "HELLO WORLD"))
__evalOut__ = __evalOut__ ::: List((2, "HELLO WORLD" .toLowerCase == "hello world"))
// fill in this method so that a string of words gets capitalized
def capitalize(s: String) = {
  def capitalizeWord( w : String ) = w(0).toUpper + w.substring(1, w.length).toLowerCase
  val wordList = s.split("\\s").toList.map( capitalizeWord )
  wordList.reduceLeft(_ + " " + _)
}
__evalOut__ = __evalOut__ ::: List((13, capitalize("man OF stEEL") == "Man Of Steel" ))
__evalOut__"""

    val prepared = (new PreparedPuzzleString)(s)

    prepared must equalTo(expected)
    
    val response : List[Tuple2[Int,Boolean]] = PuzzleEvaluator.rawEval(prepared)
    
    println( response )
    response.length must equalTo(3)

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
