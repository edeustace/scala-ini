package models


import play.api.db._
import play.api.Play.current
import play.api.Logger
import anorm._
import anorm.SqlParser._
import play.api.libs._


case class UserSolution(id: Pk[Long], user_email:String, puzzle_id:Long, solution:String)

object UserSolution{
  
  val simple = {
    
    get[Pk[Long]]("user_solution.id") ~
    get[String]("user_solution.user_email") ~
    get[Long]("user_solution.puzzle_id") ~
    get[String]("user_solution.solution") map {
      case id~user_email~puzzle_id~solution => UserSolution(id,user_email,puzzle_id,solution)
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

   def create(email:String, puzzleId:Long, solution:String): Boolean = {
    DB.withConnection { implicit connection =>

      val totalRows = SQL(
              """
                select count(*) from user_solution 
                where user_solution.user_email = {email}
                and user_solution.puzzle_id = {puzzleId}
              """
            ).on(
              'email -> email,
              'puzzleId -> puzzleId
            ).as(scalar[Long].single)

      if( totalRows == 0 )
      {
        SQL(
          """
            insert into user_solution
            (user_email, puzzle_id, solution)
            values ( {email}, {puzzleId}, {solution} )
          """
        ).on(
          'email -> email,
          'puzzleId -> puzzleId,
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
