package controllers

import play.api._
import play.api.mvc._
import models._
import models.Problem
import scala.tools.nsc._
import com.twitter.util.Eval
import play.api.http.ContentTypes
import play.api.libs.json._
import play.api.libs.json.Json._
import play.api._
import play.api.mvc._

object Problems extends Controller with Secured {
  

  def ping() = Action{ request =>

   
    val map : Map[String,Seq[String]] = request.body.asFormUrlEncoded.getOrElse(Map())
    
    val one : Seq[String] = map.getOrElse("one", List[String]())
    val two : Seq[String] = map.getOrElse("two", List[String]())
    
    Ok( 
      toJson( 
        JsObject(
          List(
            "one"->JsString(one.head),
            "two"->JsString(two.head)
          )
        )
      )
    )
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

  def testPage() = Action{ implicit request => 
    Ok(views.html.problems.testPage())
  }
  def show(id:String) = Action{ implicit request => 

    val userIsLoggedIn : Boolean = isLoggedIn(request)
    Logger.debug("user is logged in: " + userIsLoggedIn)
    
    Ok(views.html.problems.show("username", Problem.findById(id.toLong), getUser(request)))
  }

  def solveRaw() = Action { implicit request => 
    val solution : String = getFormParameter( request, "solution") 
    val responseObject = _solve(solution)
    Ok( toJson( responseObject) )
  }

  private def buildResponse( success : Boolean, t : Tuple2[String,String]* ) : JsObject = {
    
    var list : List[Tuple2[String,JsString]]= List()
    for( tuple <- t) {
      val newTuple = (tuple._1, JsString(tuple._2))
      list = list:::List(newTuple)
    }

    JsObject(
     List("success"-> JsBoolean(success)):::list
    )
  }

  private def _solve( solution : String ) : JsObject = {
  
    solution match {
      case null => buildResponse(false, ("solution", solution))
      case _ => {
        
        if( solution isEmpty )
        {
          return buildResponse(false, ("message", "solution is empty"))
        }

        Logger.debug("Problems._solve :: apply: ["+ solution+"]")
        
        var result = false
        var message = ""

        try
        {
          result = (new Eval).apply[Boolean](solution)
        }
        catch
        {
          case ex: Exception => message = ex.getMessage() 
        }

        buildResponse(result, ("message", message), ("solution", solution))
      }
    }
  }

  private def getFormParameter(  request : Request[AnyContent], name : String ) : String = {
    val map : Map[String,Seq[String]] = request.body.asFormUrlEncoded.getOrElse(Map())
    map.getOrElse(name, List[String]()).head
  }

  def solve() = Action{ implicit  request =>
    
    val id : String = getFormParameter( request, "id" )
    val solution : String = getFormParameter( request, "solution") 
    
    Logger.debug("Problems.solve ::: id: " + id + " solution: ["+solution+"]")
    
    val problem : Problem = Problem.findById(id.toLong)
    val test = problem.tests
    
    if( solution.isEmpty )
    {
      Ok(
        toJson(
          buildResponse(false, ("solution", solution))
	      )    
      )
    }
    else
    {
      val withSolution : String = test.replace("?", solution )
	    var result : Boolean = false;
	    var exceptionMessage : String = ""
	    try
	    {
        Logger.debug("Problems.solve :: apply: ["+ withSolution+"]")
	    	result = (new Eval).apply[Boolean](withSolution)
	    }
	    catch
	    {
	      case ex: Exception => exceptionMessage = ex.getMessage() 
	    }

      if( result )
      {
        val user : User = getUser(request)

        if( user != null )
        {
          var success : Boolean = UserSolution.create(user.email, id.toLong, solution)
        }
      }
      else
      {
        if( exceptionMessage.isEmpty )
        {
          exceptionMessage = "not equal [" + withSolution + "]"
        }
      }
      
	    
	    Ok(
	        toJson(
	            JsObject(
	                List(  "success"->JsBoolean(result), 
                        "solution"->JsString(solution), 
                        "exception" -> JsString(exceptionMessage) 
                      )
	            )
	        )
	    )
	    
    }
  }
  
}
