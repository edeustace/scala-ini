
package com.ee 

import scala.util.matching.Regex

/**
 * The harvested string with some additional information relating to is position in the source string.
 */
case class DecomposedString(value:String,line:Int)

object LineHarvester
{

  /**
   * builds up a list of string lists.
   * It starts at the first line and adds ensuing lines
   * until it reaches a line that is preceded by the endListSearchTerm value.
   * At this point it starts from the start again, skips over the the line that was added
   * in the previous list and continues again until it reaches a line that is after the endListSearchTerm value
   * Eg: "a,b,c,!,d,!,e" returns => (a,b,c,d),(a,b,c,e)
   * @param multilineString - the multiline string
   * @param endListSearchTerm - the endListSearchTerm value that triggers a new list to be harvested
   * @return a list of string lists
   */
  def harvestLines( multilineString : List[String], endListSearchTerm : String ) : List[DecomposedString] = {
    
    val searchTermIndices = getIndicesFor(multilineString, endListSearchTerm)

    var out : List[DecomposedString] = List()

    searchTermIndices.foreach( (index:Int) => {

      val stack = List(buildContentUpToIndex(index, multilineString, searchTermIndices))
      out = out ::: stack 
    }) 
       
    out
  }

  private def buildContentUpToIndex( maxIndex : Int, mainList : List[String], tagIndicess : List[Int]) : DecomposedString = {
    val content : List[String] = for{
      (s,i) <- mainList.zipWithIndex 
      if i <= maxIndex 
      if i == maxIndex || tagIndicess.indexOf(i) == -1
      if !s.isEmpty
    } yield s

    DecomposedString(toMultiline(content.filter(!_.trim.isEmpty)),maxIndex)
  }

  private def toMultiline( s : List[String]) : String = s.reduceLeft(_ + "\n" + _)

  private def getIndicesFor( multilineString : List[String], searchTerm : String ) : List[Int] = {
    for{
        (s,i) <- multilineString.zipWithIndex
        if s.contains(searchTerm)
    } yield i
  }

  private def getIndicesForString(s:String, endListSearchTerm:String) : List[Int] = s match {
     case null => List()
     case _ => getIndicesFor(s.split("\n").toList, endListSearchTerm)

   }
    
 
}