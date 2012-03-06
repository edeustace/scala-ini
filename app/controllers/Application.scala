package controllers

import com.codahale.jerkson.Json._
import com.ee._
import com.ee.BrowserCheck

import models._
import models._
import models.Problem
import play.api.data.Forms._
import play.api.data._
import play.api.libs.json.Json._
import play.api.mvc._
import play.api.mvc._
import play.api.mvc._
import play.api._
import play.api._
import play.api._
import views._




object Application extends Controller {

  case class BrowserRestrict[A](action: Action[A]) extends Action[A] {
  
    def apply(request: Request[A]): Result = {
      request.headers.get("USER-AGENT") match {
        case Some(userAgent) => {
          BrowserCheck.isPermitted( userAgent ) match {
            case true => action(request)
            case false => Redirect("/not_supported")
          }
        }
        case _ => action(request)
      }
    }
    
    lazy val parser = action.parser	
    
  }
  
  // -- Authentication

  val loginForm = Form(
    tuple(
      "email" -> text,
      "password" -> text
    ) verifying ("Invalid email or password", result => result match {
      case (email, password) => User.authenticate(email, password).isDefined
    })
  )

  val signUpForm = Form(
    tuple(
      "email" -> text,
      "name" -> text,
      "password" -> text
      ) verifying( "Email already exists", result => result match {
        case (email, name, password) => User.signUp(email, name, password).isDefined
      })
    )

  /**
   * Login page.
   */
  def login = BrowserRestrict {
    Action { implicit request =>
    	Ok(html.login(loginForm))
    }
  }

  def signup = BrowserRestrict{
	  Action{ implicit request => 
	  	Ok(html.signup(signUpForm))
  	}
  }

  def completeSignup = Action{ implicit request => 
    signUpForm.bindFromRequest.fold(
      formWithErrors => BadRequest(html.signup(formWithErrors)),
      user => Redirect(routes.Problems.index).withSession("email" -> user._1)
      )
  }

  /**
   * Handle login form submission.
   */
  def authenticate = Action { implicit request =>
    loginForm.bindFromRequest.fold(
      formWithErrors => BadRequest(html.login(formWithErrors)),
      user => Redirect(routes.Problems.index).withSession("email" -> user._1)
    )
  }

  /**
   * Logout and clean the session.
   */
  def logout = Action {
    Redirect(routes.Application.login).withNewSession.flashing(
      "success" -> "You've been logged out"
    )
  }

  // -- Javascript routing

  def javascriptRoutes = Action {
    import routes.javascript._
    Ok(
      Routes.javascriptRouter("jsRoutes")(
        Problems.index, Problems.show, Problems.testAuthentication
      )
    ).as("text/javascript") 
  }

  def notSupported = Action {
    Ok(views.html.notSupported())
  }
}

/**
 * Provide security features
 */
trait Secured {
  
  /**
   * Retrieve the connected user email.
   */
  private def username(request: RequestHeader) = request.session.get("email")

  /**
   * Redirect to login if the user in not authorized.
   */
  private def onUnauthorized(request: RequestHeader) = Results.Redirect(routes.Application.login)
  

  /**
   * return a user or null
   */
  def getUser(request:RequestHeader) : User = {
   
    if( !isLoggedIn(request))
    {
      return null 
    }

    username(request) match{
      case None => null
      case Some(x) => { 
        User.findByEmail(x) match{
          case None => null
          case Some(x) => x
        }
      }
    }
  }

  def isLoggedIn(request : RequestHeader) : Boolean =  request.session.get("email") != null
  // --
  
  /** 
   * Action for authenticated users.
   */
  def IsAuthenticated(f: => String => Request[AnyContent] => Result) = Security.Authenticated(username, onUnauthorized) { user =>
    Action(request => f(user)(request))
  }

  /**
   * Check if the connected user is a member of this project.
   */
  def IsMemberOf(project: Long)(f: => String => Request[AnyContent] => Result) = IsAuthenticated { user => request =>
    if(Problem.isUserOwner(project, user)) {
      f(user)(request)
    } else {
      Results.Forbidden
    }
  }

}

