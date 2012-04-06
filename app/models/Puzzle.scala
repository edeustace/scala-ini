package models

import java.util.{Date}

import com.ee.string.RandomString

import play.api.db._
import play.api.Play.current

import anorm._
import anorm.SqlParser._


/**
 * Masks tagged results with a ?
 * so 'true == OPEN_TAG true CLOSE_TAG' becomes 'true == ?'
 */
object PuzzleMasker {
  def mask(s:String) : String = {

    val expr = """(?s)/\*<\*/.*?/\*>\*/""".r
    expr.replaceAllIn( s, "?" )
  }
}

case class Puzzle(id: Pk[Long], 
                  name: String, 
                  description: String, 
                  body: String, 
                  level : String, 
                  category : String, 
                  user_email : String,
                  user_name : String)

case class NewPuzzle(name: String, 
                  description: String, 
                  body: String, 
                  level : String, 
                  category : String, 
                  user_email : String )

object PuzzleCaseClassHelper{

  def anonymousPuzzle( body : String ) : NewPuzzle = {
    NewPuzzle("anonymous",
              "anonymous",
              body,
              "anonymous",
              "anonymous",
              "ed.eustace@gmail.com")

  }
}


/**
 * Helper for pagination.
 */
case class Page[A](items: Seq[A], page: Int, offset: Long, total: Long) {
  lazy val prev = Option(page - 1).filter(_ >= 0)
  lazy val next = Option(page + 1).filter(_ => (offset + items.size) < total)
}

object Puzzle {
  

  /**
   * Check if a user is a member of this project
   */
  def isUserOwner(id: Long, userEmail: String): Boolean = {
    DB.withConnection { implicit connection =>
      SQL(
        """
          select * from puzzle where id = {id} and user_email = {userEmail}
        """
      ).on(
        'id -> id,
        'userEmail -> userEmail
      ).as(scalar[Boolean].single)
    }
  }
  
  /**
   * Parse a Puzzle from a ResultSet
   */
  val simple = {
    get[Pk[Long]]("puzzle.id") ~
    get[String]("puzzle.name") ~
    get[String]("puzzle.description") ~
    get[String]("puzzle.body") ~
    get[String]("puzzle.level") ~
    get[String]("puzzle.category") ~
    get[String]("puzzle.user_email") ~
    get[String]("app_user.name")  map {
      case id~name~description~body~level~category~user_email~user_name 
        => Puzzle(id, name, description, body, level, category, user_email, user_name)
    }
  }
 
  
  
  
  /**
   * Retrieve a puzzle from the id.
   */
  def findById(id: Long, maskSolution:Boolean = true): Puzzle = {
    val opt : Option[Puzzle] = DB.withConnection { implicit connection =>
      SQL("""select * from puzzle as puzzle 
              inner JOIN app_user as app_user
              on app_user.email = puzzle.user_email
              where id = {id}""").on('id -> id).as(Puzzle.simple.singleOpt )
    }
    opt match {
      case None => null
      case Some(p) => if( maskSolution ) mask(p) else p
    }
  }

  /**
   * Find the puzzle by the public url key
   */
  def findByUrlKey(key:String, maskSolution:Boolean = true) : Puzzle = {
  
    val opt : Option[Long] = DB.withConnection { implicit connection => 
    
      SQL("""
        select puzzle_id from puzzle_url_key as url_key
        where key = {key}
        """).on('key -> key).as(scalar[Long].singleOpt)
    }

    opt match {
      case None => null
      case Some(id) => {
        println("found puzzle id: " + id + " for key: " + key)
        findById(id, maskSolution)
      }
    }
  }

  /**
   * mask the puzzle solution
   */
  private def mask(p : Puzzle ) : Puzzle 
    = Puzzle(p.id, 
          p.name, 
          p.description, 
          PuzzleMasker.mask(p.body), 
          p.level, 
          p.category, 
          p.user_email, 
          p.user_name)
 
  /**
   * Return a page of (Puzzle,Company).
   *
   * @param page Page to display
   * @param pageSize Number of puzzles per page
   * @param orderBy Puzzle property used for sorting
   * @param filter Filter applied on the name column
   */
  def list(page: Int = 0, 
          pageSize: Int = 100,
          orderBy: Int = 1, 
          filter: String = ""): Page[(Puzzle)] = {
    
    val offset = pageSize * page
    
    DB.withConnection { implicit connection =>
      
      val puzzles = SQL(
        """
          select * from puzzle as puzzle 
              inner JOIN app_user as app_user
              on app_user.email = puzzle.user_email
          where puzzle.name like {filter} and puzzle.is_on_curriculum = true
          order by 1
          limit {pageSize} offset {offset}
        """
      ).on(
        'pageSize -> pageSize, 
        'offset -> offset,
        'filter -> filter,
        'orderBy -> orderBy
      ).as(Puzzle.simple *)

      val totalRows = SQL(
        """
          select count(*) from puzzle 
          where puzzle.name like {filter}
        """
      ).on(
        'filter -> filter
      ).as(scalar[Long].single)
      val masked = puzzles.map( mask )
      Page(masked, page, offset, totalRows)
      
    }
    
  }
  
  /**
   * Update a puzzle.
   *
   * @param id The puzzle id
   * @param puzzle The puzzle values.
   */
  def update(id: Long, puzzle: Puzzle) = {
    DB.withConnection { implicit connection =>
      SQL(
        """
          update puzzle
          set name = {name}, description = {description}, body = {body}
          where id = {id}
        """
      ).on(
        'id -> id,
        'name -> puzzle.name,
        'description -> puzzle.description,
        'body -> puzzle.body
      ).executeUpdate()
    }
  }
  
  /**
   * Insert a new puzzle.
   *
   * @param puzzle The puzzle values.
   * @return a tuple (new id of puzzle, urlKey for puzzle)
   */
  def insert(puzzle: NewPuzzle) = {
    DB.withConnection { implicit connection =>
      val insertedId = SQL(
        """
          insert into puzzle (name,description,body,level,category,user_email) 
            values (
            {name}, {description}, {body}, {level}, {category}, {user_email}
          ) returning id"""
      ).on(
        'name -> puzzle.name,
        'description -> puzzle.description,
        'body -> puzzle.body,
        'level -> puzzle.level,
        'category -> puzzle.category,
        'user_email -> puzzle.user_email
      ).as(scalar[Long].single)

      val urlKey = (new RandomString)(8)
      println("urlKey: " + urlKey)
      val result = SQL(
        """
          insert into puzzle_url_key (puzzle_id, key)
          values({puzzleId}, {urlKey}) """
        ).on(
          'puzzleId -> insertedId,
          'urlKey -> urlKey
          ).executeUpdate()

        (insertedId, urlKey)
    }
  }
  
  /**
   * Delete a puzzle.
   *
   * @param id Id of the puzzle to delete.
   */
  def delete(id: Long) = {
    DB.withConnection { implicit connection =>
      SQL("delete from puzzle where id = {id}").on('id -> id).executeUpdate()
    }
  }
  
}


