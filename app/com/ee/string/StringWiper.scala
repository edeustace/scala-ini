package com.ee.string

import scala.util.matching.Regex


object StringWiper {
  
  /**
   * processes a part of a string with the provided action,
   * the part that is processed will bet marked in the taggedTemplate as being between the openTag and closeTag 
   *
   * so: 
   * "hello there harry", "hello <> harry", (_.replace('e', 'a')), "<", ">" will return
   * "hello thara harry"
   *
   */
  def wipe( original:String, taggedTemplate : String, action: (String) => String, openTag : String, closeTag : String ) : String = {

    val WholeTagRegex = ("(?s)" + openTag + ".*?" + closeTag).r
    //val WholeTagRegex = ("" + openTag + ".*" + closeTag).r
    val solutionRegex = 
      WholeTagRegex
        .replaceAllIn(taggedTemplate, "___match_here___" )
        .replace("{", "\\{")
        .replace("}", "\\}")
        .replace("(", "\\(")
        .replace(")", "\\)")
        .replace("___match_here___", ")(.*?)(")
    
    val MatchSolution =  new Regex("(?s)(.*" + solutionRegex + ".*)")
    println(MatchSolution)
    val MatchSolution( pre, solution, post ) = original
    println(pre)
    println(solution)
    println(post)
    pre + action(solution) + post 
  }
}
