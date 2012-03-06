package test.com.ee

import org.specs2.mutable._
import play.api.test._
import play.api.test.Helpers._
import scala.collection.mutable.HashMap

class BrowserCheckSpec extends Specification {
  
  import com.ee.BrowserCheck

  
  "BrowserCheck" should {
    
     val userAgents : List[Tuple2[String,Boolean]] = List(
         
      ("", false),
      ("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/535.21 (KHTML, like Gecko) Chrome/19.0.1042.0 Safari/535.21", true),
      ("Mozilla/5.0 (Macintosh; Intel Mac OS X 10.6; rv:9.0) Gecko/20100101 Firefox/9.0", true),
      ("Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10_6_8; de-at) AppleWebKit/533.21.1 (KHTML, like Gecko) Version/5.0.5 Safari/533.21.1", true),
      ("Mozilla/5.0 (compatible; MSIE 10.0; Windows NT 6.1; Trident/6.0)", false),
      ("Mozilla/5.0 (Android 2.2; Windows; U; Windows NT 6.1; en-US) AppleWebKit/533.19.4 (KHTML, like Gecko) Version/5.0.3 Safari/533.19.4", false),
      ("Mozilla/5.0 (iPod; U; CPU iPhone OS 4_3_3 like Mac OS X; ja-jp) AppleWebKit/533.17.9 (KHTML, like Gecko) Version/5.0.2 Mobile/8J2 Safari/6533.18.5", false),
      ("Mozilla/5.0(iPad; U; CPU iPhone OS 3_2 like Mac OS X; en-us) AppleWebKit/531.21.10 (KHTML, like Gecko) Version/4.0.4 Mobile/7B314 Safari/531.21.10gin_lib.ccMozilla/5.0(iPad; U; CPU iPhone OS 3_2 like Mac OS X; en-us) AppleWebKit/531.21.10 (KHTML, like Gecko) Version/4.0.4 Mobile/7B314 Safari/531.21.10gin_lib.cc", false)
      )
      
      val result = for( tuple <- userAgents ) yield (tuple._1, BrowserCheck.isPermitted(tuple._1) == (tuple._2) )
      
      val failedItems = result.filter(_._2 == false )
      
      failedItems.foreach( (t:Tuple2[String,Boolean]) => println("failed: " + t._1))
      
      "should all pass" in {
        
        failedItems.length must equalTo(0)
        
      }
     }
   
}
