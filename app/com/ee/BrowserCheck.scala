package com.ee

object BrowserCheck {
  
  def isPermitted( userAgent : String ) : Boolean = userAgent match {
    case null => false
    case s : String if userAgent.isEmpty => false
    case s : String if !userAgent.isEmpty => {

     if( isMobile(s)){
       return false
     }

     s.contains("Firefox") || 
     s.contains("Chrome") ||
     s.contains("Safari")
    }
    case _ => false
  }

  private def isMobile(userAgent:String) : Boolean = userAgent match {
  
    case null => false
    case s : String if userAgent.isEmpty => false
    case s : String if !userAgent.isEmpty => {
    
      s.contains("iPad") ||
      s.contains("iPod") ||
      s.contains("Mobile") ||
      s.contains("Android")
    }
    case _ => false
  }

}
