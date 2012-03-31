package controllers

import scala.util.matching.Regex

import com.codahale.jerkson.Json.generate
import com.ee.EvaluationResult
import com.ee.PuzzleEvaluator
import com.ee.string.TaggedStringProcessor
import com.ee.puzzle.PuzzleRegex

import controllers.Application.BrowserRestrict

import models.NewPuzzle
import models.Puzzle
import models.PuzzleCaseClassHelper
import models.User
import models.UserSolution
import play.api.mvc.Request
import play.api.mvc.Action
import play.api.mvc.AnyContent
import play.api.mvc.Controller
import play.api.Logger

case class ResponseWithMessage(successful:Boolean, message : String, id:Long = -1, urlKey:String = "")
case class ResponseWithResults(successful:Boolean, result : EvaluationResult)

object Puzzles extends Controller with Secured {
  
  
  val Json = ("Content-Type" -> "application/json; charset=utf-8")
  
  val SUCCESS = "success"
  val MESSAGE = "message"
  val INVALID_SOLUTION_SYNTAX = "Error: you need to add " + PuzzleRegex.BEGIN + " and " + PuzzleRegex.END
  val MISSING_ALL_FORM_VARS = "You must supply a name, description, level and category"
  val UNKNOWN_ERROR = "An unknown error occured"
  val SUBMITTED = "submitted!"

  val EXPECTED_TRUE_GOT_FALSE = "Expected true but got false"


  object Params {
    val SOLUTION = "solution"
    val ID = "id"
    val DESCRIPTION = "description"
    val NAME = "name"
    val LEVEL = "level" 
    val CATEGORY = "category"
  }

  /**
   * pages
   */
  def createPuzzlePage = BrowserRestrict{ Action{
      Ok(views.html.puzzles.createPuzzle(PuzzleRegex.BEGIN,  PuzzleRegex.END))
  }}
  
  
  /**
   * @deprecated('submit will be gone once the save mechanism is in place')
   */
  def submitPuzzlePage = IsAuthenticated { username => _ =>
    User.findByEmail(username).map{ user => 
      Ok(views.html.puzzles.submitPuzzle(PuzzleRegex.BEGIN,  PuzzleRegex.END))
    }.getOrElse(Forbidden)
  }
  
  /**
   * Show a puzzle by the public url key
   */
  def showByUrlKey(key:String) = Action{ implicit request =>
    Ok(views.html.puzzles.showAnonymous(Puzzle.findByUrlKey(key)))
  }


  /**
   * @deprecated
   * Show a puzzle by puzzle id
   */
  def show(id:String) = Action{ implicit request => 
    val userIsLoggedIn : Boolean = isLoggedIn(request)
    Logger.debug("user is logged in: " + userIsLoggedIn)
    Ok(views.html.puzzles.show("username", Puzzle.findById(id.toLong), getUser(request)))
  }

  /**
   * Display the paginated list of puzzles.
   *
   * @param page Current page number (starts from 0)
   * @param orderBy Column to be sorted
   * @param filter Filter applied on computer names
   */
  def index(page: Int = 0, orderBy:Int = 1, filter: String = "") = BrowserRestrict {
    Action { implicit request =>

      val user : User = getUser(request)
      var solutions : Seq[UserSolution] = List()
      if( user != null ){
        solutions = UserSolution.findSolutionsByEmail(user.email)  
      }
      
      Ok(views.html.puzzles.list(
        "Scala Puzzles",
        Puzzle.list(page = page, orderBy = orderBy, filter = ("%"+filter+"%")),orderBy, filter, user, solutions )
      )
    }
  }


  
  /**
   * Save a puzzle, anonymous users can save puzzles and they'll get a url key that they can then link to.
   */
  def save = Action{ implicit request => 
    val response = processSolution(insertPuzzleOnly)  
    Ok(generate(response)).withHeaders(Json)
  }
  

  /**
   * Solve a supplied solution
   * @param solution the puzzle solution
   */
  def solve() = Action { implicit request => 

    getFormParameters( Params.SOLUTION, Params.ID )  match {
      case List(None,None) => Ok( generate( ResponseWithMessage(false, "No solution provided")) )
      case List(Some(solution), None) => {

        //Note: we don't pre or post process the solution as we have no reference to compare it with.
        val evaluationResponse : EvaluationResult = PuzzleEvaluator.solve(solution)
        val output = ResponseWithResults(evaluationResponse.successful, evaluationResponse)
        val generated = generate(output)
        Ok(generate(output)).withHeaders(Json)
      }
      case List(Some(solution), Some(id)) => {
        
        val preAndPostProcessTuple = getPreAndPostPreprocessFn(id.toLong)

        val evaluationResponse : EvaluationResult 
          = PuzzleEvaluator.solve(solution, 
                Option(preAndPostProcessTuple._1), 
                Option(preAndPostProcessTuple._2))

        if(evaluationResponse.successful){
          storeSolutionIfLoggedIn(solution)
        }
        val output = ResponseWithResults(evaluationResponse.successful, evaluationResponse)
        val generated = generate(output)
        
        Ok(generate(output)).withHeaders(Json)
      }
      case _ => Ok(generate(ResponseWithMessage(false, "Unknown Error")))
    }
  }

  
  /**
   * The PuzzleEvaluator accepts a pre and post string processor function.
   * These functions manipulate the string before and after it is evaluated.
   * In this instance we remove '==' within the user solution if they've been added by the user.
   * If they are part of the original solution they are maintained as this is how the evaluations 
   * are done.
   * @param id the id of the Puzzle in the db. 
   * @return a Tuple2 of the pre and post processing functions
   */
  private def getPreAndPostPreprocessFn(id:Long) : Tuple2[String => String, String => String] = {

        val taggedSolution : String = Puzzle.findById(id, false).body
        
        /**
         * replace first item in tuple with second item
         * @param token to escape
         * @param replacement
         * @param isEscape - if true will swap replacement for token otherwise it'll do the opposite.
         * @param s - the string to act on
         * @return the manipulated string
         */
        def escape(first:String,second:String)(isEscape:Boolean)(s:String) = {
          val searchItem =  if( isEscape ) first else second 
          val replacement = if( isEscape ) second else first 
          s.replace(searchItem, replacement)
        }
        
        val changeEquals = escape("==", "_!double_eq!_")(_)
        val escapeEquals = changeEquals(true)(_)
        val unEscapeEquals = changeEquals(false)(_)


        def preProcessFn(template:String)(solution:String) : String = {
           TaggedStringProcessor.process(solution, template, escapeEquals, "/*<*/", "/*>*/" )
        }

        (preProcessFn(taggedSolution)(_), unEscapeEquals)
  }

  /**
   * Store the user solution if they are logged in
   */
  private def storeSolutionIfLoggedIn(solution:String)(implicit request : Request[AnyContent] )  = {
    val user : User = getUser(request)
    if( user != null){
      getFormParameter( Params.ID ) match {
        case None =>
        case Some(id) => UserSolution.create(user.email, id.toLong, solution)
      }
    }
  }

   
  def solveAndSubmit() = Action { implicit request =>
    val response = processSolution(extractParamsAndInsert)  
    Ok( generate(response)).withHeaders(Json)
  }

  /**
   * @param: validPuzzleHandler - the function to invoke of the solution is valid.
   * @return: a ResponseWithMessage
   */
  private def processSolution( validPuzzleHandler : String => ResponseWithMessage )(implicit request : Request[AnyContent])= {
    getFormParameter( Params.SOLUTION )  match {
      
      case None => ResponseWithMessage(false, "No solution provided")
      
      case Some(solution) => {
      
        val result : EvaluationResult = PuzzleEvaluator.solve(solution)
          
        if( result.successful ){
          if( PuzzleRegex.isValid(solution)){
            validPuzzleHandler(solution)
          }
          else{
            ResponseWithMessage(false, INVALID_SOLUTION_SYNTAX)
          }
        }else{
          ResponseWithMessage(false, UNKNOWN_ERROR)
        }
      }
    }
  }

  /**
   * Extract the remaining parameters and insert into db.
   */
  private def extractParamsAndInsert(solution:String) (implicit request : Request[AnyContent] ) 
    : ResponseWithMessage  = { 
    getFormParameters( Params.NAME, Params.DESCRIPTION, Params.LEVEL, Params.CATEGORY) match {
   
      case List(Some(name), Some(description), Some(level), Some(category)) => {
        
        val resultTuple = insertNewPuzzle(solution,name,description,level,category) 
        ResponseWithMessage(true,SUBMITTED, resultTuple._1)
      }
      case _ => ResponseWithMessage(false, MISSING_ALL_FORM_VARS)
    }
  }

  /**
   * only insert the puzzle - we don't have any user data
   */
  private def insertPuzzleOnly(solution:String)(implicit request : Request[AnyContent] ) 
    : ResponseWithMessage = { 
    val resultTuple = Puzzle.insert(PuzzleCaseClassHelper.anonymousPuzzle(solution))
    ResponseWithMessage(true,SUBMITTED, resultTuple._1, resultTuple._2)
  }

  private def insertNewPuzzle(solution:String, name:String, description:String, level : String, category:String )(implicit request : Request[AnyContent] )  = {
    val user : User = getUser(request)
    val u : Option[User] = User.findByEmail(request.session.get("email").getOrElse("ed.eustace@gmail.com"))
    val email = u.getOrElse(User()).email
    
    val newPuzzle =  NewPuzzle( name, description, solution, level, category, email)

    Puzzle.insert( newPuzzle )
  }


  //TODO: Tidy this up
  private def getFormParameters( names : String* )( implicit request : Request[AnyContent]) : List[Option[String]] = {
	  
      val l = names.toArray[String].toList
      l match {
      case List() => List()
      case _ => List(getFormParameter(l.head)) ::: getFormParameters(l.tail.toArray[String] : _*)
    }
  }

  private def getFormParameter( name : String )(implicit request : Request[AnyContent] ) : Option[String] = {
    val map : Map[String,Seq[String]] = request.body.asFormUrlEncoded.getOrElse(Map())
    val resultList : Option[Seq[String]] = map.get(name)
    resultList match {
      case None => None
      case Some(list) => Some(list.head)
    }
  }

  
}
