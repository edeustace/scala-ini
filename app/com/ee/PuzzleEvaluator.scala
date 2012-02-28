package com.ee 

import com.twitter.util.Eval
import play.api._

case class EvaluationResult(successful:Boolean, summary:String, evaluations : List[SingleEvaluationResult] = List()  )
case class SingleEvaluationResult(successful:Boolean = true, line:Int = -1, message : String = "")

object PuzzleEvaluator
{
  
  val EVALUATE_TAG = "/*!evaluate!*/"

  object Error{
    val EMPTY_STRING = "empty string"
    val NULL_STRING = "null string"
  }

  val ResultSummary = "Evaluated {count}. successful: {successful} failed: {failed}"

  val CompilationException = "Could not compile"

  object Failed {
    val FAILED = "Evaluation failed"
  }
  
  val MESSAGE = "message"

  def solve( solution : String ) : EvaluationResult = {
  
    def getCompilationException(s:String ) : Option[Exception] = {
      try{
        (new Eval).apply[Boolean](s)
      }catch{
        case ex: Exception => return Option(ex) 
      }
      None
    } 

    solution match {
      case null => EvaluationResult(false, Error.NULL_STRING)
      case s : String if s.isEmpty => EvaluationResult(false, Error.EMPTY_STRING)
      case s : String if !s.isEmpty => {

        getCompilationException(solution) match{

          case Some(exception) => EvaluationResult(false, CompilationException )
          case None => {
            val toEvaluate: List[DecomposedString] = decomposeSolution(s)
            val evaluations : List[SingleEvaluationResult] = toEvaluate.map( applyEvaluation )
            val successful = evaluations.filter(_.successful).length
            val failed = evaluations.length - successful 
            EvaluationResult(successful == evaluations.length, getSummary(successful,failed),evaluations)
          }
        }
      }
    }
  }

  def getSummary(successful:Int, failed : Int) : String = {
    ResultSummary
      .replace("{count}", (successful + failed).toString )
      .replace("{successful}", successful.toString )
      .replace("{failed}", failed.toString )

  }
  
  private def decomposeSolution( solution : String ) : List[DecomposedString] = {
   
    def toMultiline( l : List[String]) : String = l.reduceLeft(_ + "\n" + _) 
    val EQUALITY_CHECK = "=="
    val lines = solution.split("\n").toList 


    def isSingleEvaluation( s:String) : Boolean = {
      val equalsIndex = s.split("\n").indexWhere( _.contains(EQUALITY_CHECK)) 
      equalsIndex == lines.length || equalsIndex == -1
    }

    solution match {
      case s : String if isSingleEvaluation(s) => List(DecomposedString(s,s.length))
      case _ => LineHarvester.harvestLines(lines, EQUALITY_CHECK)    
    }
  }

  private def applyEvaluation( decomposedString : DecomposedString ) : SingleEvaluationResult = {

      val result = (new Eval).apply[Boolean](decomposedString.value)
      result match {
        case true => SingleEvaluationResult(true, decomposedString.line)  
        case false => SingleEvaluationResult(false, decomposedString.line) 
      }
  }
}