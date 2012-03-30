package com.ee.string

import scala.util.Random

/**
 * Generates a random string of a given length:
 * val s = RandomString(2) //A0
 * val t = RandomString(3) //a9I
 */
class RandomString {
  
  def apply(length:Int) : String = {
    val chars = ('a' to 'z') ++ ('A' to 'Z') ++ ('0' to '9') ++ ("-!£$")
    def isUnique(s:String):Boolean = true
    uniqueRandomKey(chars.mkString(""), 8, isUnique)
  }


  private def uniqueRandomKey(chars: String, length: Int, uniqueFunc: String=>Boolean) : String = { 
    val newKey = (1 to length).map(
        x =>
        {
          val index = Random.nextInt(chars.length)
          chars(index)
        }
        ).mkString("")
    if (uniqueFunc(newKey)) 
      newKey
    else
      uniqueRandomKey(chars, length, uniqueFunc)
  }
}
