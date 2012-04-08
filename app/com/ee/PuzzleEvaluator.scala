package com.ee

import com.twitter.util.Eval
import play.api._

case class EvaluationResult(successful:Boolean, summary:String, evaluations : List[SingleEvaluationResult] = List()  )
case class SingleEvaluationResult(successful:Boolean = true, line:Int = -1, message : String = "")

object PreparedPuzzleString
{

  object TAGS {
    val INDEX = "{index}"
    val BOOLEAN = "{boolean}"
    val LINES = "{lines}"
  }

  val SINGLE_EVAL = "__evalOut__ = __evalOut__ ::: List(({index}, {boolean}))"
  val PREAMBLE = """//this is generated code
//to allow for a batch evaluation
//declare the list
"""
  val TEMPLATE = PREAMBLE + """
var __evalOut__ : List[Tuple2[Int,Boolean]] = List()
{lines}
__evalOut__"""
}

class PreparedPuzzleString
{
  def apply(s:String) : String = s match {
    case s if s == null || s.isEmpty => s
    case _ => {
      val lines = s.split("\\n").toList 
      

      def isEvaluable(l:String) : Boolean = {
        val out = l.contains("==") || l.trim.equals("true") || l.trim.equals("false")
        out
      }
      def buildTuple(line:String, index:Int) : String = {
        
        val out : String = PreparedPuzzleString.SINGLE_EVAL
          .replace( PreparedPuzzleString.TAGS.INDEX, index.toString)
          .replace(PreparedPuzzleString.TAGS.BOOLEAN, line )
        out
      }
      
      val prepared = for{ 
        (l,i) <- lines.zipWithIndex
        out = if(isEvaluable(l)) buildTuple(l,i) else l
      } yield out
      val preparedLines = prepared.filter(!_.isEmpty).mkString("\n")
      val out = PreparedPuzzleString.TEMPLATE.replace("{lines}", preparedLines)
      out
    }
  }
}

object PuzzleEvaluator
{
  object Error{
    val EMPTY_STRING = "empty string"
    val NULL_STRING = "null string"
    val UNSAFE_SOLUTION = "Unsafe solution - does your solution contain any of the following: " + UnsafeStrings.mkString(", ") + "?"
  }

  val ResultSummary = "Evaluated {count}. successful: {successful} failed: {failed}"

  val CompilationException = "Could not compile the code you submitted -"


  val UnsafeStrings : List[String] = List("system", 
      "exit",
      "db",
      "sql",
      "throw",
      "exception")

  object Failed {
    val FAILED = "Evaluation failed"
  }
  
  def rawEval( value : String ) : List[Tuple2[Int,Boolean]] = {
      (new Eval).apply[List[Tuple2[Int,Boolean]]](value)
  }
  
  def solve(  solution : String, 
              prePrepare : Option[String => String] = None, 
              postPrepare : Option[String => String] = None ) : EvaluationResult = {
  
    def getCompilationException(code:String ) : Option[Exception] = {
      try{
        (new Eval).apply[Boolean](code)
      }catch{
        case ex: Exception => return Option(ex) 
      }
      None
    } 

    solution match {
      case null => EvaluationResult(false, Error.NULL_STRING)
      case s : String if s.isEmpty => EvaluationResult(false, Error.EMPTY_STRING)
      case s : String if !s.isEmpty && !isSafe(s) => EvaluationResult(false, Error.UNSAFE_SOLUTION)
      case s : String if !s.isEmpty => {

        try {
          _prepareSolutionAndEval(solution, prePrepare, postPrepare)    
        }
        catch {
          case ex : Exception => {
            getCompilationException(solution) match {
              case Some(exception) => EvaluationResult(false, CompilationException + ": " + exception.getMessage)
              case None => {
                Logger.error("Error in derived code")
                EvaluationResult(false, "Compilation Exception in the derived code") 
              }
            } 
          }
        }
      }
    }
    
  }

  def isSafe(solution:String) : Boolean = {
    for( unsafeString <- UnsafeStrings){
      if( solution.toLowerCase.contains(unsafeString)){
        return false
      }
    }
    true
  }


  /**
   * Prepare the solution string and eval it.
   * Our eval technique is to:
   * 1. search for any line that contains '=='
   * 2. place those lines into a tuple that contains the original line number and the evaluation
   * 3. Execute an eval the expects a List[Tuple2[Int,Boolean]] response.
   *
   * Note that there is some pre and post processing of the string being prepared.
   * This is due to the user solution potentially containing '==' that should be ignored.
   * TODO: This should be removed from here.
   *
   */
  private def _prepareSolutionAndEval(s:String, prePrepare : Option[String => String], postPrepare : Option[String => String]) : EvaluationResult = {

    val prePrepared : String = prePrepare match {
      case Some(fn) => fn(s)
      case _ => s
    }
    
    val prepared : String = (new PreparedPuzzleString)(prePrepared)
    
    val postPrepared = postPrepare match {
      case Some(fn) => fn(prepared)
      case _ => prepared
    }
    
    val result : List[Tuple2[Int,Boolean]] = rawEval(postPrepared)
    val evaluations = result.map((t:Tuple2[Int,Boolean]) => SingleEvaluationResult(t._2, t._1))
    val successful = evaluations.filter(_.successful).length
    val failed = evaluations.length - successful 
    EvaluationResult(successful == evaluations.length, getSummary(successful,failed),evaluations)
  }

  def getSummary(successful:Int, failed : Int) : String = {
    ResultSummary
      .replace("{count}", (successful + failed).toString )
      .replace("{successful}", successful.toString )
      .replace("{failed}", failed.toString )

  }
}
