package test

import org.specs2.mutable._
import play.api.test._
import play.api.test.Helpers._
import play.api.mvc.Action
import play.api.mvc.AnyContent
import play.api.Logger


class StringWiperSpec extends Specification {
  
  import com.ee.string._
  
  val EQUALS_CHECK = "=="

  
  "StringWiper" should {
    
    "wipe the string" in {
    
      val tweak = (s:String) => s.replace("=", "!")
      val c = StringWiper.wipe("x == y, blah", "x <..>, blah", tweak, "<", ">")
      c must equalTo( "x !! y, blah") 
    }
    /*

    TODO:...
    "wipe a puzzle" in {

      val str = """def isZero(x:Int) : Boolean = {
          x == 2

          y == 2
        }

        isZero(1) == false 
        isZero(0) == true"""

      val template = """def isZero(x:Int) : Boolean = {
          /*<*/ /*>*/ 

          /*<*/ /*>*/ 
        }

        isZero(1) == false 
        isZero(0) == true"""

      val expected = """def isZero(x:Int) : Boolean = {
          x _!eq!_ 2 

          y _!eq!_ 2 
        }

        isZero(1) == false 
        isZero(0) == true"""


      val escapeEquals = (s:String) => s.replace("=", "_!eq!_")
      cleaned must equalTo(expected)
    }  
    */
  }
   
}
