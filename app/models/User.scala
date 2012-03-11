package models

import play.api.db._
import play.api.Play.current
import play.api.Logger
import anorm._
import anorm.SqlParser._
import play.api.libs._

case class User(email: String = "", name: String = "", password: String = "", solutionCount : Long = -1)

object User {
  
  // -- Parsers
  
  val weird = {
    get[String]("current_user") map {
      case current_user => User(current_user)
    }
  }
  /**
   * Parse a User from a ResultSet
   */
  val simple = {
    get[String]("email") ~
    get[String]("name") ~
    get[String]("password") map {
      case email~name~password => User(email, name, password)
    }
  }

  val withSolutioncount = {
    get[String]("user.email") ~
    get[String]("user.name") ~
    get[String]("user.password") ~
    get[Long]("user.solution_count") map {
      case email~name~password~solution_count 
        => User(email, name, password, solution_count)
    }
  }
  
  // -- Queries
  
  /**
   * Retrieve a User from email.
   */
  def findByEmail(email: String): Option[User] = {

    //val query = "select * from \"user\" where email = '{email}'"
    val query = "select * from \"user\" as u where u.email = '{email}'"
    val result = DB.withConnection { implicit connection =>

      val q =  SQL(query).on(
        'email -> email
      )
      
      

      println( "q: " + q)
      SQL(query).on(
        'email -> email
      ).as(User.simple.singleOpt)
    }

    println(result)
    result
  }

 
  
  /**
   * Retrieve all users.
   */
  def findAll: Seq[User] = {
    DB.withConnection { implicit connection =>
      SQL("select * from \"user\"").as(User.simple *)
    }
  }


  def signUp(email:String, name:String, password:String) : Option[User] = {
    
    val existingUser = findByEmail(email)
    Logger.debug("email : " + email + ", existingUser: "+ existingUser)

    existingUser match{ 
      case None => {
        Logger.debug("no user found = create one..")
        Some(
          create( new User(email, name, password) )
        )

      }
      case Some(x) => {
        Logger.debug("user already exists")
        None
      }
    }
  }

      
  
  /**
   * Authenticate a User.
   */
  def authenticate(email: String, password: String): Option[User] = {
    DB.withConnection { implicit connection =>
      SQL(
        """
         select * from user where 
         email = {email} and password = {password}
        """
      ).on(
        'email -> email,
        'password -> Codecs.sha1(password)
      ).as(User.simple.singleOpt)
    }
  }
   
  /**
   * Create a User.
   */
  def create(user: User): User = {
    DB.withConnection { implicit connection =>
      SQL(
        """
          insert into user values (
            {email}, {name}, {password}
          )
        """
      ).on(
        'email -> user.email,
        'name -> user.name,
        'password -> Codecs.sha1(user.password)
      ).executeUpdate()
      
      user
      
    }
  }

  def updatePassword(email:String, password:String) : Int = {
    DB.withConnection { implicit connection =>
      SQL(
        """
          update user
          set password = {password}
          where email = {email}
        """
      ).on(
        'email -> email,
        'password -> password
      ).executeUpdate()
    }

  }

   /**
   * Return a page of (Problem,Company).
   *
   * @param page Page to display
   * @param pageSize Number of problems per page
   * @param orderBy Problem property used for sorting
   * @param filter Filter applied on the name column
   */
  def list(page: Int = 0, 
          pageSize: Int = 10,
          orderBy: Int = 1, 
          filter: String = ""): Page[(User)] = {
    
    val offset = pageSize * page
    
    DB.withConnection { implicit connection =>
      
      val users = SQL(
        """
          select * from user 
          where user.name like {filter}
          order by {orderBy} nulls last
          limit {pageSize} offset {offset}
        """
      ).on(
        'pageSize -> pageSize, 
        'offset -> offset,
        'filter -> filter,
        'orderBy -> orderBy
      ).as(User.simple *)

      val totalRows = SQL(
        """
          select count(*) from user 
          where user.name like {filter}
        """
      ).on(
        'filter -> filter
      ).as(scalar[Long].single)

      Page(users, page, offset, totalRows)
    }
  }
  
}
