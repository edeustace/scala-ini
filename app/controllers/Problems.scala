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
            "one"->JsString(one.first),
            "two"->JsString(two.first)
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
      "list of items",
      Problem.list(page = page, orderBy = orderBy, filter = ("%"+filter+"%")),orderBy, filter, user, solutions )

    )
  }

  def show(id:String) = Action{ implicit request => 

    val userIsLoggedIn : Boolean = isLoggedIn(request)
    Logger.debug("user is logged in: " + userIsLoggedIn)
    
    Ok(views.html.problems.show("username", Problem.findById(id.toLong), getUser(request)))
  }

  def solve() = Action{ implicit  request =>
    
    val map : Map[String,Seq[String]] = request.body.asFormUrlEncoded.getOrElse(Map())
    
    val id : String= map.getOrElse("id", List[String]()).first
    val solution : String = map.getOrElse("solution", List[String]()).first
    Logger.debug("Problems.solve ::: id: " + id + " solution: ["+solution+"]")
    val problem : Problem = Problem.findById(id.toLong)
    val test = problem.tests
    val withSolution : String = test.replace("?", solution )
    
    if( solution.isEmpty )
    {
      Ok(
        toJson(
          JsObject(
	                List("success"->JsBoolean(false), 
                        "solution"->JsString(solution), 
                        "blahblah" -> JsString("blahbbbbbb")
                      )
	            )    
        )
      )
    }
    else
    {
	    var result : Boolean = false;
	    var exceptionMessage : String = ""
	    try
	    {
        Logger.debug("Problems.solve :: test: ["+test+"]")
        Logger.debug("Problems.solve :: solution: ["+solution+"]")
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
          var success : Boolean = UserSolution.create(user.email, id.toLong)
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
