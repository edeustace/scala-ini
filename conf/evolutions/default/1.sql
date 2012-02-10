# --- First database schema

# --- !Ups

set ignorecase true;

create table user (
  email                     varchar(255) not null primary key,
  name                      varchar(255) not null,
  password                  varchar(255) not null
);

create table problem (
  id 						bigint not null,
  name 					varchar(255) not null,
  description 	varchar(255) not null,
  tests 				varchar(255) not null,
  level 				varchar(255) not null,
  category 			varchar(255) not null,
  user_email		varchar(255) not null,
  foreign key(user_email)   references user(email) on delete cascade,
  constraint 		pk_problem primary key (id))
;


create table user_solution(
  id  bigint not null primary key auto_increment,
  user_email varchar(255) not null,
  problem_id varchar(255) not null,
  foreign key(user_email) references user(email) on delete cascade,
  foreign key(problem_id) references problem(id) on delete cascade
 
);


# --- !Downs

SET REFERENTIAL_INTEGRITY FALSE;

drop table if exists problem;
drop table if exists user;


