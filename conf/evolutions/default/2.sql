# --- Sample dataset

# --- !Ups

insert into user (email,name,password) values ('ed.eustace@gmail.com','ed eustace','password');

insert into problem (id,name,description,tests,level,category,user_email) 
	values (1,'True is true','Is it?','? == true','easy', 'boolean', 'ed.eustace@gmail.com');
insert into problem (id,name,description,tests,level,category,user_email) 
	values (2,'false is false','Is it?','? == 10 - (2 * 3)','easy','boolean', 'ed.eustace@gmail.com');
insert into problem (id,name,description,tests,level,category,user_email) 
	values (3,'Uppercase','Is it?','? == "Hello World".toUpperCase','easy', 'string', 'ed.eustace@gmail.com');
insert into problem (id,name,description,tests,level,category,user_email) 
	values (4,'Adding lists','Is it?','? == List(1,2) ::: List(3,4)','easy', 'lists', 'ed.eustace@gmail.com');
insert into problem (id,name,description,tests,level,category,user_email) 
	values (5,'Access list item','Is it?','? == List(1,2,3)(2)','easy', 'lists', 'ed.eustace@gmail.com');
insert into problem (id,name,description,tests,level,category,user_email) 
	values (6,'Dropping Lists','Is it?','? == List(1,2,3,4).drop(2)','easy', 'lists', 'ed.eustace@gmail.com');
insert into problem (id,name,description,tests,level,category,user_email)
	values (7,'Double up', 'write a function that returns a double', '? (2) == 4', 'easy', 'function', 'ed.eustace@gmail.com');
insert into problem (id,name,description,tests,level,category,user_email)
	values (8,'Hello World', 'write a function that returns a personalized greeting', '? ("Dave") == "Hello, Dave!"', 'easy', 'function', 'ed.eustace@gmail.com');


insert into user_solution (user_email,problem_id)
		values ('ed.eustace@gmail.com',1);

insert into user_solution (user_email,problem_id)
		values ('ed.eustace@gmail.com',2);

# --- !Downs
delete from problem;
