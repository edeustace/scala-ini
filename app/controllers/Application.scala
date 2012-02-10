package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._

import models._
import views._

object Application extends Controller {

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
  def login = Action { implicit request =>
    Ok(html.login(loginForm))
  }

  def signup = Action{ implicit request => 
    Ok(html.signup(signUpForm))
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

