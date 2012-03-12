import play.api._
import play.api.libs._
import models._
import models.User$

object Global extends GlobalSettings {
  
  override def onStart(app: Application) {
    InitialData.insert()
  }

  object InitialData {

    def insert() = {

      def hashPassword(email:String) = {
        //TODO: Move the data insertion to another point
        //Where: 
        //1. It should only happen once when the app boots
        //2. It shouldn't re-hash the password if its already hashed
        
        val u = User.findByEmail(email)
        val user = u.getOrElse(User())
        u match {
          case None => println("ignoring: " + email)
          case Some(u) => {
            val hashed = Codecs.sha1(u.password)
            println("Found user and updating password...: " + u)
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
