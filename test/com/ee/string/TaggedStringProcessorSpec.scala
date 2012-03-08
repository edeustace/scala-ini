package test

import org.specs2.mutable._
import play.api.test._
import play.api.test.Helpers._
import play.api.mvc.Action
import play.api.mvc.AnyContent
import play.api.Logger

import _root_.com.ee.string._

class TaggedStringProcessorSpec extends Specification {
  
  
  val EQUALS_CHECK = "=="

  val RemoveEqEq = (s:String) => s.replace("==", "_!double_eq!_")
  
  "TaggedStringProcessor" should {
    
    "process the string" in {
    
      val tweak = (s:String) => s.replace("=", "!")
      val c = TaggedStringProcessor.process("x == y, blah", "x <..>, blah", tweak, "<", ">")
      c must equalTo( "x !! y, blah") 
    }


    "if an error is thrown it returns the source string" in {
      val tweak = (s:String) => s.replace("=", "!")
      val c = TaggedStringProcessor.process("false", "x <..>, blah", tweak, "<", ">")
      c must equalTo( "false") 
    }


    def assertProcess(base : String, fn : String => String, expectedChangeList : List[String]) = {
      val solution = base.replace("/*<*/", "").replace("/*>*/", "")
      var expected = solution

      for( s <- expectedChangeList ){
        expected = expected.replace(s, fn(s))
      }

      val cleaned = TaggedStringProcessor.process(solution, base, fn, "/*<*/", "/*>*/")
      cleaned must equalTo(expected)
    }


    "process a simple puzzle" in {

      val base = """def isZero(x:Int) : Boolean = {
          /*<*/x == 2/*>*/
        }
        isZero(0) == true"""
      
      val expectedChanges : List[String] = List("x == 2")
      assertProcess(base, RemoveEqEq, expectedChanges)
    }  

    "process a more complex puzzle" in {
    
      val base = """/*Inspired by: http://www.4clojure.com/problem/27 */
    /*Warning: For now, dont use two equals eg: [= =] in the method body. use equals() instead*/
def isPalindrome( l : List[Any] ) : Boolean = l match {
    /*<*/
    case List() => true
    case List(one) => true
    case List(one,two) => one == two
    case _ => l.head == l.last && isPalindrome(l.tail.init)
    /*>*/
}


isPalindrome( List(1,2,1) ) == true
isPalindrome( List(1,2,3) ) == false
isPalindrome( List(1,2,2,1) ) == true
/*<*/ here is a cheeky == ? /*>*/
isPalindrome( List("a", "b", "b", "a")) == true
isPalindrome( List("c", "a", "r")) == false
"""
  assertProcess(base, RemoveEqEq, List("cheeky == ?", "one == two", "l.head == l.last"))
    
    }

    "can process despite regex chars" in {

      val base = """

def isOne(m:Int) : Boolean = /*<*/m == 1/*>*/
\
?
*
^
.
{}
()
|
$
def three() = 1 + 2
"""
      assertProcess(base, RemoveEqEq, List("m == 1"))


    }
    
  }
   
}
