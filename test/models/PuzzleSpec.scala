package test

import org.specs2.mutable._

import play.api.test._
import play.api.test.Helpers._

class PuzzleSpec extends Specification {
  
  import models._


  "Puzzle model" should {
    
    "be retrieved by id" in {
      running(FakeApplication(additionalConfiguration = SpecHelper.testDb())) {
        
        val trueIsTrue = Puzzle.findById(1)
        trueIsTrue.user_email must equalTo("ed.eustace@gmail.com")
      }
    }

    "can insert anonymous puzzle and retrieve with url key" in {
      running(FakeApplication(additionalConfiguration = SpecHelper.testDb())) {
        
        val body = """//custom anonymous puzzle for url key
        true == /*<*/true/*>*/"""
        val resultTuple = Puzzle.insert( 
          PuzzleCaseClassHelper.anonymousPuzzle(body) 
        )
        val p = Puzzle.findByUrlKey(resultTuple._2, false)
        p.body must equalTo(body)
      }

    }
    
    
    "can insert anonymous puzzle" in {
      running(FakeApplication(additionalConfiguration = SpecHelper.testDb())) {
        
        val body = """//custom anonymous puzzle
        true == /*<*/true/*>*/"""
        val resultTuple = Puzzle.insert( 
          PuzzleCaseClassHelper.anonymousPuzzle(body) 
        )
        val p = Puzzle.findById(resultTuple._1, false)
        p.body must equalTo(body)
      }

    }
  } 

  "puzzleMasker" should {

    "replace all tags" in {

      PuzzleMasker.mask("/*<*/hello/*>*/") must equalTo("?")


      val masked = PuzzleMasker.mask("""/*<*/hello/*>*/ 
        /*<*/hel
        lo/*>*/ 
        /*<*/hello/*>*/""") 

      println("masked: [" + masked + "]")
      masked.count(_ == '?' ) must equalTo(3)
    }
  }   
  
}
