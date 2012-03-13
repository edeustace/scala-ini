package models


import play.api.db._
import play.api.Play.current
import play.api.Logger
import anorm._
import anorm.SqlParser._
import play.api.libs._


case class UserSolution(id: Pk[Long], user_email:String, problem_id:Long, solution:String)

object UserSolution{
  
  val simple = {
    
    get[Pk[Long]]("user_solution.id") ~
    get[String]("user_solution.user_email") ~
    get[Long]("user_solution.problem_id") ~
    get[String]("user_solution.solution") map {
      case id~user_email~problem_id~solution => UserSolution(id,user_email,problem_id,solution)
    }
  }

  /**
   * Find all the users solutions
   */
  def findSolutionsByEmail(email:String = null) : Seq[UserSolution] = {

    Logger.debug("findSolutionsByEmail: email: " + email)
    if( email.isEmpty )
    {
      return List()
    }
    DB.withConnection{ implicit connection => 

        SQL("select * from user_solution where user_email = {email}").on(
            'email -> email
          ).as(UserSolution.simple *)
    }

  }

   def create(email:String, problemId:Long, solution:String): Boolean = {
    DB.withConnection { implicit connection =>

      val totalRows = SQL(
              """
                select count(*) from user_solution 
                where user_solution.user_email = {email}
                and user_solution.problem_id = {problemId}
              """
            ).on(
              'email -> email,
              'problemId -> problemId
            ).as(scalar[Long].single)

      if( totalRows == 0 )
      {
        SQL(
          """
            insert into user_solution
            (user_email, problem_id, solution)
            values ( {email}, {problemId}, {solution} )
          """
        ).on(
          'email -> email,
          'problemId -> problemId,
          'solution -> solution
        ).executeUpdate()
      
        Logger.debug("created user solution")
        true
      }else
      {
        Logger.debug("already solved - ignore create")
        true
      }
      
    }
  }


  

}
