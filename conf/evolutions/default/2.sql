# --- Sample dataset

# --- !Ups

insert into user (email,name,password) values ('ed.eustace@gmail.com','ed eustace','password');
insert into user (email,name,password) values ('edeustace@yahoo.com','ed eustace','password');

insert into problem (id,name,description,body,level,category,user_email) 
	values (1,'The basics','Some simple tests to get you started','//booleans
true == /*<*/true/*>*/
false == /*<*/false/*>*/

//arithmetic
1 + 1 == /*<*/2/*>*/
1 + 2 * 3 == /*<*/7/*>*/
(1 + 2) * 3 == /*<*/9/*>*/
6 / 2 - 1 == /*<*/2/*>*/
6 / (2 - 1) == /*<*/6/*>*/',
'easy', 'basics', 'ed.eustace@gmail.com');


insert into problem (id,name,description,body,level,category,user_email) 
  values (2,'String','Some basic string manipulations',
'"hello world" .toUpperCase == /*<*/"HELLO WORLD"/*>*/

"HELLO WORLD" /*<*/.toLowerCase/*>*/ == "hello world"

// fill in this method so that a string of words gets capitalized
def capitalize(s: String) = {
/*<*/
  def capitalizeWord( w : String ) = w(0).toUpper + w.substring(1, w.length).toLowerCase
  val wordList = s.split("\\s").toList.map( capitalizeWord )
  wordList.reduceLeft(_ + " " + _)
/*>*/
}

capitalize("man OF stEEL") == "Man Of Steel"'
,
'easy', 'string', 'ed.eustace@gmail.com');

insert into problem (id,name,description,body,level,category,user_email) 
  values (3,'Functions','Some basic functions',
'//def defines a function
def addOne(m: Int): Int = m + 1

addOne(1) == /*<*/2/*>*/
addOne(2) == /*<*/3/*>*/

// now you write the function...
def addTwo(m:Int):Int = /*<*/m + 2/*>*/

addTwo(1) == 3
addTwo(2) == 4

def three() = 1 + 2

three == /*<*/3/*>*/'
,
'easy', 'functions', 'ed.eustace@gmail.com');

insert into problem (id,name,description,body,level,category,user_email) 
  values (4,'Anonymous Functions','Some anonymous functions',
'/*inspired by http://twitter.github.com/scala_school/basics.html */
//we can assign an anonymous function to a val
val addOne = (x: Int) => x + 1

addOne(1) == /*<*/2/*>*/
addOne(2) == /*<*/3/*>*/

//or inline
((x:Int) => x + 1)(5) == /*<*/6/*>*/
((x:Int) => x + 1)(8) == /*<*/9/*>*/

//your turn:
(/*<*/(x:Int) => x - 2/*>*/)(5) == 3
(/*<*/(x:Int) => x * 2/*>*/)(6) == 12

'
,
'easy', 'functions', 'ed.eustace@gmail.com');

insert into problem (id,name,description,body,level,category,user_email) 
  values (5,'Partial application of functions','Some examples of partially applied functions',
'/* inspired by http://twitter.github.com/scala_school/basics.html */
//You can partially apply a function with an underscore, which gives you another function.
def adder(m: Int, n: Int) = m + n
val add2 = adder(2, _:Int)

add2(1) == /*<*/3/*>*/
add2(2) == /*<*/4/*>*/

//your turn...
val add3 = /*<*/adder(3, _:Int)/*>*/
add3(1) == 4
add3(2) == 5
'
,
'easy', 'functions', 'ed.eustace@gmail.com');

insert into problem (id,name,description,body,level,category,user_email) 
  values (6,'Curried functions','Some examples of curried functions',
'/* inspired by http://twitter.github.com/scala_school/basics.html */
def multiply(m: Int)(n: Int): Int = m * n

multiply(2)(3) == /*<*/6/*>*/

val timesTwo = multiply(2)(_)

timesTwo(3)  == /*<*/6/*>*/

//your turn...
val timesFour = /*<*/multiply(4)(_)/*>*/
timesFour(4) == 16
timesFour(2) == 8

//how to curry a normal function?
def adder(m:Int, n:Int) = m + n
val adderCurried = (adder(_,_))/*<*/.curried/*>*/
adderCurried(1)(2) == 3
'
,
'easy', 'functions', 'ed.eustace@gmail.com');

  insert into problem (id,name,description,body,level,category,user_email) 
  values (7,'Traits','Simple traits',
'/* inspired by http://twitter.github.com/scala_school/basics.html */
trait And {
    def and() : String = "whatever!"
}

class Ying extends And {
    override def and() : String = "yang"
}

//write a class called Birds
/*<*/
class Birds extends And {
  override def and() : String = "bees"
}
/*>*/

val ying = new Ying()
ying.and == "yang"

val birds = new Birds()
birds.and == "bees"
'
,
'easy', 'traits', 'ed.eustace@gmail.com');

insert into problem (id,name,description,body,level,category,user_email)
  values (8,'Interleave', 'Write a function called "interleave" that interleaves 2 lists', 
    'def interleave( a: List[Any], b: List[Any] ) : List[Any] = {
    /*<*/
       a match{
      case List() => List()
      case _ => {
        b match{
          case List() => List()
          case _ => {
            List(a.head,b.head) ::: interleave(a.tail,b.tail)
          }
        }
      }
    }
    /*>*/
}
val resultOne = interleave(List(1,2,3,4),List("a","b","c","d"))
val expectedOne = List(1,"a",2,"b",3,"c",4,"d")
resultOne == expectedOne

val resultTwo = interleave(List(1,2,3,4,5), List("a","b"))
val expectedTwo = List(1,"a",2,"b")
resultTwo == expectedTwo', 'simple', 'functions', 'ed.eustace@gmail.com');

  insert into problem (id,name,description,body,level,category,user_email)
  values (9,'Lists', 'Some simple list puzzles', 
    '
    val myList = List(1,2,3,4,5,6)
    myList.map( _ + 5 ) == /*<*/(6,7,8,9,10,11)/*>*/
    myList.filter(_ > 4) == /*<*/List(5,6)/*>*/
    myList.head == /*<*/1/*>*/
    myList.last == /*<*/11/*>*/
    myList.drop(4) == /*<*/List(5,6)/*>*/
    ', 'simple', 'functions', 'ed.eustace@gmail.com');




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
		values ('ed.eustace@gmail.com',1, '//booleans
true == true
false == false

//arithmetic
1 + 1 == 2
1 + 2 * 3 == 7
(1 + 2) * 3 == 9
6 / 2 - 1 == 2
6 / (2 - 1) == 6');

insert into user_solution (user_email,problem_id, solution)
		values ('ed.eustace@gmail.com',2, '"hello world" .toUpperCase == "HELLO WORLD"

"HELLO WORLD" .toLowerCase == "hello world"

// fill in this method so that a string of words gets capitalized
def capitalize(s: String) = {
    def capitalizeWord( w : String ) = w(0).toUpper + w.substring(1, w.length).toLowerCase
  val wordList = s.split("\\s").toList.map( capitalizeWord )
  wordList.reduceLeft(_ + " " + _)
}

capitalize("mAn OF STeeL") == "Man Of Steel"');

# --- !Downs
delete from problem;
delete from user_solution;
delete from user;
