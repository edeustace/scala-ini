# --- Sample dataset

# --- !Ups

insert into user (email,name,password) values ('ed.eustace@gmail.com','ed eustace','password');

insert into problem (id,name,description,tests,level,category,user_email) 
	values (1,'True is true','What is true equal to?','? == true','easy', 'boolean', 'ed.eustace@gmail.com');
insert into problem (id,name,description,tests,level,category,user_email) 
	values (2,'Simple math','Can you do some simple math?','10 - (2 * 3) == ?','easy','boolean', 'ed.eustace@gmail.com');
insert into problem (id,name,description,tests,level,category,user_email) 
	values (3,'Uppercase','Uppercase a string','"hello world" ? == "HELLO WORLD"','easy', 'string', 'ed.eustace@gmail.com');
insert into problem (id,name,description,tests,level,category,user_email) 
	values (4,'Adding lists','Add lists with the ":::" operator...','? == List(1,2) ::: List(3,4)','easy', 'lists', 'ed.eustace@gmail.com');
insert into problem (id,name,description,tests,level,category,user_email) 
	values (5,'Access list item','What number is returned from index 2?','? == List(1,2,3)(2)','easy', 'lists', 'ed.eustace@gmail.com');
insert into problem (id,name,description,tests,level,category,user_email) 
	values (6,'Dropping Lists','What does drop give you?','? == List(1,2,3,4).drop(2)','easy', 'lists', 'ed.eustace@gmail.com');
insert into problem (id,name,description,tests,level,category,user_email)
	values (7,'Double up', 'Write a function that returns a double', '(?) (2) == 4', 'easy', 'function', 'ed.eustace@gmail.com');
insert into problem (id,name,description,tests,level,category,user_email)
	values (8,'Hello World', 'Write a function that returns a personalized greeting', '? ("Dave") == "Hello, Dave!"', 'easy', 'function', 'ed.eustace@gmail.com');
insert into problem (id,name,description,tests,level,category,user_email)
	values (9,'Interleave', 'Write a function called "interleave" that interleaves 2 lists', 
    'def interleave( a: List[Any], b: List[Any] ) : List[Any] = {
    ?
    }
    
    interleave(List(1,2,3,4),List("a","b","c","d")) == List(1,"a",2,"b",3,"c",4,"d")', 'simple', 'functions', 'ed.eustace@gmail.com');

insert into problem (id,name,description,tests,level,category,user_email,solution)
values( 10, 'Map', 'working with maps!',
  '/**
 * The map function takes two arguments: a function (f) and a sequence (s). 
 * Map returns a new sequence consisting of the result of applying f to each item of s. 
 * Do not confuse the map function with the map data structure.
 */
List(1,2,3).map( (_ + 5)) == ?',
  'easy',
  'map',
  'ed.eustace@gmail.com',
  'List(6,7,8)');

insert into problem (id,name,description,tests,level,category,user_email,solution)
values( 11, 'Filter',
  'fun with filter!',
  '/**
 * The filter function takes two arguments: a predicate function (f) and a sequence (s). 
 * Filter returns a new sequence consisting of all the items of s for which (f item) returns true.
 */
List(3,4,5,6,7,8).filter(_ > 5) == ?',
  'easy',
  'map',
  'ed.eustace@gmail.com',
  'List(6,7,8)');

insert into problem (id,name,description,tests,level,category,user_email,solution)
values( 12, 'Last element',
  'Write a function which returns the last element in a sequence. Note: dont use last',
  '(?)( List(1,2,3,4) ) == 4',
  'easy',
  'map',
  'ed.eustace@gmail.com',
  '(l:List) => l(l.length -1)');

insert into problem (id,name,description,tests,level,category,user_email,solution)
  values(13, 'Find attribute with value', 'Write a function that returns a list of nodes that contain an attribute with a given value',
    '
    import scala.xml._

    def getNodesWithAttributeValue( node : Node, value : String ) : List[Node] = {
      ?
    }

    val xml = <div>
        <span class="test">hello</span>
        <div class="test"><p>hello</p></div>
      </div>
    
    getNodesWithAttributeValue(xml, "test") == 
            List[Node](
              <span class="test">hello</span>, 
              <div class="test"><p>hello</p></div>
            )
    ',
    'easy', 'function', 'ed.eustace@gmail.com',
  '
  //solution
def getNodesWithAttributeValue( node : Node, stringVal : String ) : List[Node] = 
{
    def attributeValueEquals(value: String)(node: Node) = {
        node.attributes.exists(_.value.text == value)  
    }
    //get all nodes and subnodes
    val ns : NodeSeq = node \\ "_"
    ns.filter( attributeValueEquals(stringVal) ).toList
}
  
  ');

insert into problem (id,name,description,tests,level,category,user_email,solution)
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
// write method here
// updateVersion( node : Node ) : Node)
?

val updatedXml : Node = updateVersion(InputXml)
(updatedXml\"subnode"\"version" text)  == "2" && (updatedXml\"contents"\"version" text) == "1"

',
'easy', 
'function', 
'ed.eustace@gmail.com',
'
def updateVersion( node : Node ) : Node = {
   def updateElements( seq : Seq[Node]) : Seq[Node] = 
     for( subNode <- seq ) yield updateVersion( subNode )  

   node match {
     case <root>{ ch @ _* }</root> => <root>{ updateElements( ch ) }</root>
     case <subnode>{ ch @ _* }</subnode> => <subnode>{ updateElements( ch ) }</subnode>
     case <version>{ contents }</version> => <version>2</version>
     case other @ _ => other
   }
 }
');

insert into user_solution (user_email,problem_id, solution)
		values ('ed.eustace@gmail.com',1, 'true');

insert into user_solution (user_email,problem_id, solution)
		values ('ed.eustace@gmail.com',2, '4');

# --- !Downs
delete from problem;
delete from user_solution;
delete from user;
