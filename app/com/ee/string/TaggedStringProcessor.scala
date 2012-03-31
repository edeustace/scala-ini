package com.ee.string

import scala.util.matching.Regex


object TaggedStringProcessor {
  
  val TMP_TAG = "__" + scala.math.floor( scala.math.random * 1000).toInt + "__"
  
  /**
   * processes a part of a string with the provided action,
   * the part that is processed will be marked in the normalisedTemplate as being between the openTag and closeTag 
   *
   * so: 
   * "hello there harry", "hello <> harry", (_.replace('e', 'a')), "<", ">" will return
   * "hello thara harry"
   *
   */
  def process( original:String, template : String, action: (String) => String, openTag : String, closeTag : String ) : String = {

    val MatchAnyNonGreedy = "(.*?)"

    val escapedOpen = escapeRegexChars(openTag)
    val escapedClose = escapeRegexChars(closeTag)

    def buildRegexFromTemplate(template:String) : Regex = {
      val templateWithoutTags = processTemplate(template)
      buildRegexFromTemplateWithoutTags(templateWithoutTags)
    }
    
    def processTemplate( template : String ) : List[String] = {
      val normalised = normaliseTagParts(template, TMP_TAG, escapedOpen, escapedClose)
      val escapedAndNormalised = escapeRegexChars(normalised)
      escapedAndNormalised.split(TMP_TAG).toList
    }
    
    /**
     * Replace a tagged zone of varying size with a standard key eg:
     * text <blah> text <blah blah>
     * into:
     * text _TMP_KEY_ text _TMP_KEY_
     */
    def normaliseTagParts( s : String, key : String, open : String, close : String) = {
      val WholeTagRegex = ("(?s)" + open + ".*?" + close).r
      WholeTagRegex.replaceAllIn(s, key )
    }

    /**
     * Build out the Regex that will be used to match the normalised groups
     */
    def buildRegexFromTemplateWithoutTags( list : List[String]) : Regex = {
      val start = "(?s)(" + list.head + ".*?)"
      val middleList = list.tail.dropRight(1)
      val middle = buildMiddleRegexString(middleList)
      val end = "(" + list.last + ".*)"
      val altogether = start + middle + end
      altogether.r
    }
    
    def buildMiddleRegexString( list : List[String]) : String = list match {
      case List() => MatchAnyNonGreedy
      case _ => MatchAnyNonGreedy + list.map("(" + _ + ")" ).mkString(MatchAnyNonGreedy) + MatchAnyNonGreedy
    }
   
    /**
     * Process the matched groups in the middle.
     * All odd groups should be processed and all even groups should be left alone
     */
    def processMiddleMatches( list : Seq[String], fn : String => String) : String = {
      val processedList = for( (s,index) <- list.zipWithIndex ) yield if(index % 2 != 0) s else fn(s)
      processedList.mkString
    }

    val CodeRegex = buildRegexFromTemplate(template)
    
    try{
      val CodeRegex(matchedGroups @ _*) = original
      val body = processMiddleMatches( matchedGroups.tail.dropRight(1), action)
      matchedGroups.head + body + matchedGroups.last
    }
    catch{
      case ex : Exception => original
    }
    
    
  }

  /**
   * Escape regex chars eg:
   * '*hello*' 
   * into:
   * '\*hello\*'
   */
  private def escapeRegexChars( s : String ) : String = {
    val EscapeChars = "{}()[].*|+$^?\\"
    val EscapePattern = "([" + EscapeChars.toCharArray().toList.map("\\" + _).mkString("|")+ "])"
    EscapePattern.r.replaceAllIn(s, "\\\\$1")
  }

  def log( key:String, value : Any ) = { 
    println("\n--------------------\n" + key + "\n- - - -\n" + value.toString + "\n- - - - - - - - - - - ")
  }
}
