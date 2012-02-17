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
	values (7,'Double up', 'Write a function that returns a double', '? (2) == 4', 'easy', 'function', 'ed.eustace@gmail.com');
insert into problem (id,name,description,tests,level,category,user_email)
	values (8,'Hello World', 'Write a function that returns a personalized greeting', '? ("Dave") == "Hello, Dave!"', 'easy', 'function', 'ed.eustace@gmail.com');
insert into problem (id,name,description,tests,level,category,user_email)
	values (9,'Interleave', 'Write a function called "interleave" that interleaves 2 lists', 
    '?
    
    interleave(List(1,2,3,4),List("a","b","c","d")) == List(1,"a",2,"b",3,"c",4,"d")', 'simple', 'functions', 'ed.eustace@gmail.com');

insert into problem (id,name,description,tests,level,category,user_email,solution)
  values(10, 'Find attribute with value', 'Write a function that returns a list of nodes that contain an attribute with a given value',
    '
    import scala.xml._

    /**
     * getNodesWithAttributeValue( node : Node, value : String ) : List[Node]
     */
    ?

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

insert into user_solution (user_email,problem_id, solution)
		values ('ed.eustace@gmail.com',1, 'true');

insert into user_solution (user_email,problem_id, solution)
		values ('ed.eustace@gmail.com',2, '4');

# --- !Downs
delete from problem;
delete from user_solution;
delete from user;
