package test

import org.specs2.mutable._
import play.api.test._
import play.api.test.Helpers._
import play.api.mvc.Action
import play.api.mvc.AnyContent
import play.api.Logger


class LineHarvesterSpec extends Specification {
  
  import com.ee._
  import controllers._
  
  val EQUALS_CHECK = "=="

  def assertList( s : String, expected : List[List[String]], tag : String = EQUALS_CHECK) = {
    val lines = s.split("\n").toList
    val result : List[DecomposedString] = LineHarvester.harvestLines( lines, tag )

    val stringResult : List[String] = result.map(_.value.trim)

    val stringList = result.map( _.value.split("\n").toList )

    stringList.map( _.map( _.trim ) )  must equalTo( expected )      
  }


  def listToString(l:List[String]) : String = l.reduceLeft(_ + "\n" + _)
  
  "LineHarvester" should {
    
    "assemble the lines" in {
     
      val s = """
      a
      b
      true == true

      b
      c
      false == false
      """
      assertList( s, List(List("a","b","true == true"), List("a","b","b","c", "false == false")))
    }  

  }
   
}
