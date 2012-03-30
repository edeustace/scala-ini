package test.com.ee.puzzle

import org.specs2.mutable._

import play.api.test._
import play.api.test.Helpers._

import com.ee.puzzle.PuzzleRegex

class PuzzleRegexSpec extends Specification with Tags {

  
  "PuzzleRegex" should {
    "be valid" in {
        val solution = """
        //can save a new puzzle 
        true == /*<*/true/*>*/"""
      PuzzleRegex.isValid(solution) must equalTo(true)
    }
  }
}

