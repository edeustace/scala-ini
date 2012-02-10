package models

import java.util.{Date}

import play.api.db._
import play.api.Play.current

import anorm._
import anorm.SqlParser._

case class Problem(id: Pk[Long], name: String, description: String, tests: String, level : String, category : String )

/**
 * Helper for pagination.
 */
case class Page[A](items: Seq[A], page: Int, offset: Long, total: Long) {
  lazy val prev = Option(page - 1).filter(_ >= 0)
  lazy val next = Option(page + 1).filter(_ => (offset + items.size) < total)
}

object Problem {
  
  // -- Parsers

  /**
   * Check if a user is a member of this project
   */
  def isUserOwner(id: Long, userEmail: String): Boolean = {
    DB.withConnection { implicit connection =>
      SQL(
        """
          select * from problem where id = {id} and user_email = {userEmail}
        """
      ).on(
        'id -> id,
        'userEmail -> userEmail
      ).as(scalar[Boolean].single)
    }
  }
  
  /**
   * Parse a Problem from a ResultSet
   */
  val simple = {
    get[Pk[Long]]("problem.id") ~
    get[String]("problem.name") ~
    get[String]("problem.description") ~
    get[String]("problem.tests") ~
    get[String]("problem.level") ~
    get[String]("problem.category") map {
      case id~name~description~tests~level~category => Problem(id, name, description, tests, level, category)
    }
  }
  
  // -- Queries
  
  /**
   * Retrieve a problem from the id.
   */
  def findById(id: Long): Problem = {
    val opt : Option[Problem] = DB.withConnection { implicit connection =>
      SQL("select * from problem where id = {id}").on('id -> id).as(Problem.simple.singleOpt )
    }
    opt.get
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
          filter: String = ""): Page[(Problem)] = {
    
    val offset = pageSize * page
    
    DB.withConnection { implicit connection =>
      
      val problems = SQL(
        """
          select * from problem 
          where problem.name like {filter}
          order by {orderBy} nulls last
          limit {pageSize} offset {offset}
        """
      ).on(
        'pageSize -> pageSize, 
        'offset -> offset,
        'filter -> filter,
        'orderBy -> orderBy
      ).as(Problem.simple *)

      val totalRows = SQL(
        """
          select count(*) from problem 
          where problem.name like {filter}
        """
      ).on(
        'filter -> filter
      ).as(scalar[Long].single)

      Page(problems, page, offset, totalRows)
      
    }
    
  }
  
  /**
   * Update a problem.
   *
   * @param id The problem id
   * @param problem The problem values.
   */
  def update(id: Long, problem: Problem) = {
    DB.withConnection { implicit connection =>
      SQL(
        """
          update problem
          set name = {name}, description = {description}, tests = {tests}
          where id = {id}
        """
      ).on(
        'id -> id,
        'name -> problem.name,
        'description -> problem.description,
        'tests -> problem.tests
      ).executeUpdate()
    }
  }
  
  /**
   * Insert a new problem.
   *
   * @param problem The problem values.
   */
  def insert(problem: Problem) = {
    DB.withConnection { implicit connection =>
      SQL(
        """
          insert into problem values (
            (select next value for problem_seq), 
            {name}, {description}, {tests}, {level}, {category}
          )
        """
      ).on(
        'name -> problem.name,
        'description -> problem.description,
        'tests -> problem.tests,
        'level -> problem.level,
        'category -> problem.category
      ).executeUpdate()
    }
  }
  
  /**
   * Delete a problem.
   *
   * @param id Id of the problem to delete.
   */
  def delete(id: Long) = {
    DB.withConnection { implicit connection =>
      SQL("delete from problem where id = {id}").on('id -> id).executeUpdate()
    }
  }
  
}


