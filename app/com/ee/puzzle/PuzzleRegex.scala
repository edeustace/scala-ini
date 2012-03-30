package com.ee.puzzle

import scala.util.matching.Regex

object PuzzleRegex{
  
  val BEGIN = "/*<*/"
  val END = "/*>*/"
  def escape( s : String ) : String = s.replace("*", "\\*")

  /**
   * must contain a begin tag some stuff and an end tag
   */
  val ValidPuzzle = new Regex("(?s)(.*)" + escape(BEGIN) + "(.*)"+ escape(END) +"(.*)")

  def isValid(s:String) : Boolean = s match {
    case ValidPuzzle(pre,body,post) => true
    case _ => false
  }
}
