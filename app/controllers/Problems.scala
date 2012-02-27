package controllers

import play.api._
import play.api.mvc._
import models._
import models.Problem
import scala.tools.nsc._
import com.twitter.util.Eval
import com.ee._
import play.api.http.ContentTypes
import play.api.libs.json._
import play.api.libs.json.Json._
import play.api._
import play.api.mvc._
import scala.util.matching.Regex
import org.codehaus.jackson.{ JsonGenerator, JsonToken, JsonParser }
import org.codehaus.jackson.`type`.JavaType
import org.codehaus.jackson.map._
import org.codehaus.jackson.map.annotate.JsonCachable
import org.codehaus.jackson.map.`type`.{ TypeFactory, ArrayType }
import org.codehaus.jackson.map.annotate.JsonCachable
import com.codahale.jerkson.Json._

case class ResponseWithMessage(successful:Boolean, message : String)
case class ResponseWithResults(successful:Boolean, result : EvaluationResult)

object Problems extends Controller with Secured {
  
  val SUCCESS = "success"
  val MESSAGE = "message"
  val INVALID_SOLUTION_SYNTAX = "Error: you need to add " + PuzzleRegex.BEGIN + " and " + PuzzleRegex.END
  val MISSING_ALL_FORM_VARS = "You must supply a name, description, level and category"
  val UNKNOWN_ERROR = "An unknown error occured"
  val SUBMITTED = "submitted!"

  val EXPECTED_TRUE_GOT_FALSE = "Expected true but got false"

  object PuzzleRegex{
    val BEGIN = "/*<*/"
    val END = "/*>*/"
    def escape( s : String ) : String = s.replace("*", "\\*")
    val ValidPuzzle = new Regex("(.*)" + escape(BEGIN) + "(.*)"+ escape(END) +"(.*)")
  }

  object Params {
    val SOLUTION = "solution"
    val ID = "id"
    val DESCRIPTION = "description"
    val NAME = "name"
    val LEVEL = "level" 
    val CATEGORY = "category"
  }

  case class Blah(name:String)
 
  def jsonTest() = Action { implicit request => 
    Ok( generate(Blah("blah")))
  }
  
  def submitPuzzlePage = IsAuthenticated { username => _ =>
    User.findByEmail(username).map{ user => 
      Ok(views.html.problems.submitPuzzle(PuzzleRegex.BEGIN,  PuzzleRegex.END))
    }.getOrElse(Forbidden)
  }
  
  /**
   * test authentication.
   */
  def testAuthentication = IsAuthenticated { username => _ =>
    User.findByEmail(username).map { user =>
      Ok("authenticated")
    }.getOrElse(Forbidden)
  }
 
  /**
   * Display the paginated list of computers.
   *
   * @param page Current page number (starts from 0)
   * @param orderBy Column to be sorted
   * @param filter Filter applied on computer names
   */
  def index(page: Int = 0, orderBy: Int = 1, filter: String = "") = Action { implicit request =>

    val user : User = getUser(request)
    var solutions : Seq[UserSolution] = List()
    Logger.debug("Problems.index :: user: " + user)
    if( user != null ){
      solutions = UserSolution.findSolutionsByEmail(user.email)  
    }
    
    Ok(views.html.problems.list(
      "Scala Puzzles",
      Problem.list(page = page, orderBy = orderBy, filter = ("%"+filter+"%")),orderBy, filter, user, solutions )
    )
  }


  def show(id:String) = Action{ implicit request => 

    val userIsLoggedIn : Boolean = isLoggedIn(request)
    Logger.debug("user is logged in: " + userIsLoggedIn)
    
    Ok(views.html.problems.show("username", Problem.findById(id.toLong), getUser(request)))
  }

  def solve() = Action { implicit request => 

    getFormParameter( Params.SOLUTION )  match {
      case None => Ok( generate( ResponseWithMessage(false, "No solution provided")) )
      case Some(solution) => {
        val evaluationResponse : EvaluationResult = PuzzleEvaluator.solve(solution)

        if( evaluationResponse.successful ){
          storeSolutionIfLoggedIn( solution )
        }

        Ok( generate( ResponseWithResults(evaluationResponse.successful, evaluationResponse) ))
      }
    }
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

    getFormParameter( Params.SOLUTION )  match {
      case None => Ok( generate( ResponseWithMessage(false, "No solution provided")))
      case Some(solution) => {

        val result : EvaluationResult = PuzzleEvaluator.solve(solution)

        if( result.successful )
        {
          solution match 
          {
            case PuzzleRegex.ValidPuzzle(pre,puzzleSolution,post) => {

              getFormParameters( Params.NAME, Params.DESCRIPTION, Params.LEVEL, Params.CATEGORY) match {
             
                case List(Some(name), Some(description), Some(level), Some(category)) => {
                  
                  //TODO: fix puzzle insertion
                  //insertNewPuzzle(solution,name,description,level,category) 
                  Ok( generate( ResponseWithMessage(true,SUBMITTED)) )
                }
                case _ => Ok( generate( ResponseWithMessage(false, MISSING_ALL_FORM_VARS)) )
              }
            }
            case _ => Ok( generate( ResponseWithMessage(false, INVALID_SOLUTION_SYNTAX)) )
          }

        }else{
          Ok( generate( ResponseWithMessage(false, UNKNOWN_ERROR)) )
        }

      }
    } 
  }

  private def insertNewPuzzle(solution:String, name:String, description:String, level : String, category:String )(implicit request : Request[AnyContent] )  = {
    val user : User = getUser(request)
    val u : Option[User] = User.findByEmail(request.session.get("email").getOrElse("ed.eustace@gmail.com"))
    val email = u.getOrElse(User()).email
    
    val newPuzzle =  NewProblem( name, description, solution, level, category, email)

    Problem.insert( newPuzzle )
  }


  //TODO: Tidy this up
  private def getFormParameters( names : String* )( implicit request : Request[AnyContent]) : List[Option[String]] = {
	  
      val l = List.fromArray(names.toArray[String])
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
