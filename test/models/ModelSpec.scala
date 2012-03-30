package test

import org.specs2.mutable._

import play.api.test._
import play.api.test.Helpers._

class ModelSpec extends Specification {
  
  import models._

  "todo" should {
    
    "be done.." in {
      "todo" must equalTo("not done")
    }.pendingUntilFixed("do i need this anymore?")
    
  }
  
}