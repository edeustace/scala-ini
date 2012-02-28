import play.api._
import play.api.libs._
import models._
import models.User$

object Global extends GlobalSettings {
  
  override def onStart(app: Application) {
    InitialData.insert()
  }
  

  object InitialData {
  
    //def date(str: String) = new java.text.SimpleDateFormat("yyyy-MM-dd").parse(str)
  
    def insert() = {

      def hashPassword(email:String) = {
        
        val u = User.findByEmail(email)
        val user = u.getOrElse(User())
        u match {
          case None => println("ignoring: " + email)
          case Some(u) => {
            val hashed = Codecs.sha1(u.password)
            User.updatePassword( u.email, hashed)
          }

        }
      }
        
      Seq(
        "ed.eustace@gmail.com",
        "edeustace@yahoo.com"
      ).foreach(hashPassword)

    }
  }
}