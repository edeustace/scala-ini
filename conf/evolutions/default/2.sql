# --- Sample dataset

# --- !Ups

insert into user (email,name,password) values ('ed.eustace@gmail.com','ed eustace','password');
insert into user (email,name,password) values ('edeustace@yahoo.com','ed eustace','password');

insert into problem (id,name,description,body,level,category,user_email) 
	values (1,'True is true','What is true equal to?','true == /*<*/true/*>*/
false == /*<*/false/*>*/

def double(x:Int) : Int = /*<*/x * 2/*>*/

double(1) == 2
double(3) == 6

"hello".toUpperCase == /*<*/"HELLO"/*>*/','easy', 'boolean', 'ed.eustace@gmail.com');
insert into problem (id,name,description,body,level,category,user_email) 
  values (2,'Simple math','Can you do some simple math?','10 - (2 * 3) == /*<*/4/*>*/','easy','boolean', 'ed.eustace@gmail.com');
insert into problem (id,name,description,body,level,category,user_email)
  values(14, 'Update one node but not another', 'Write a function "updateVersion" that updates a version node if it is within a "subnode" node.',
'
import scala.xml._

val InputXml : Node =
<root>
    <subnode>
        <version>1</version>
    </subnode>
    <contents>
        <version>1</version>
    </contents>
</root>

def updateVersion( node : Node ) : Node = {
/*<*/
   def updateElements( seq : Seq[Node]) : Seq[Node] = 
     for( subNode <- seq ) yield updateVersion( subNode )  

   node match {
     case <root>{ ch @ _* }</root> => <root>{ updateElements( ch ) }</root>
     case <subnode>{ ch @ _* }</subnode> => <subnode>{ updateElements( ch ) }</subnode>
     case <version>{ contents }</version> => <version>2</version>
     case other @ _ => other
   }
/*>*/
 }

val updatedXml : Node = updateVersion(InputXml)
(updatedXml\"subnode"\"version" text)  == "2" && (updatedXml\"contents"\"version" text) == "1"

',
'easy', 
'function', 
'ed.eustace@gmail.com');




insert into user_solution (user_email,problem_id, solution)
		values ('ed.eustace@gmail.com',1, 'true');

insert into user_solution (user_email,problem_id, solution)
		values ('ed.eustace@gmail.com',2, '4');

# --- !Downs
delete from problem;
delete from user_solution;
delete from user;
