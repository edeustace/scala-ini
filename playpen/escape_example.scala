/**
 * replace first item in tuple with second item
 * @param token to escape
 * @param replacement
 * @param isEscape - if true will swap replacement for token otherwise it'll do the opposite.
 * @param s - the string to act on
 * @return the manipulated string
 */
def escape(first:String,second:String)(isEscape:Boolean)(s:String) = {
  val searchItem =  if( isEscape ) first else second 
  val replacement = if( isEscape ) second else first 
  s.replace(searchItem, replacement)
}

val changeEquals = escape("==", "_!double_eq!_")(_)
val escapeEquals = changeEquals(true)(_)
val unEscapeEquals = changeEquals(false)(_)

println( escapeEquals( "hello == there"))
println( unEscapeEquals( "hello _!double_eq!_ there"))
println("done")

