package controllers

import play.api._
import play.api.mvc._
import models._
import models.Puzzle
import scala.tools.nsc._
import com.twitter.util.Eval
import play.api.http.ContentTypes
import play.api.libs.json._
import play.api.libs.json.Json._
import play.api._
import play.api.mvc._

object Users extends Controller with Secured
{

	  def index(page: Int = 0, orderBy: Int = 1, filter: String = "") = Action { implicit request =>

	  
	  val currentUser : User = getUser(request)
    Ok(views.html.users.list(
      "list of users",
      User.list(page = page, orderBy = orderBy, filter = ("%"+filter+"%")),orderBy, filter, currentUser)

    )
  }

  def show( email : String ) = Action{ implicit request => 

  	Ok("email: " + email)
  }
	
}