package test.ui


import org.specs2.mutable._

import play.api.test._
import play.api.test.Helpers._

class BrowserSpec extends Specification {

  "BrowserSpec" should {
  
    "run in a browser" in {
      running(TestServer(3333), HTMLUNIT) { browser =>

        browser.goTo("http://localhost:3333")
        val title = browser.$(".brand").getTexts().get(0)
        println("title: " + title)
        title must equalTo("Scala Puzzles")

      }
    }
  }

}
