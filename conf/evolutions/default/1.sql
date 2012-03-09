# --- First database schema

# --- !Ups


create table "user"(
  email                     varchar(255) not null primary key,
  name                      varchar(255) not null,
  password                  varchar(255) not null
);


create table "problem" (
  id            serial,
  name            varchar(255) not null,
  description         varchar(255) not null,
  body            varchar(8000) not null,
  level           varchar(255) not null,
  category          varchar(255) not null,
  user_email        varchar(255) not null,
  foreign key(user_email)   references "user"(email) on delete cascade,
  constraint pk_problem   primary key (id));

create table "user_solution"(
  id            serial primary key,
  user_email        varchar(255) not null,
  problem_id        bigint not null,
  solution          varchar(8000) not null,
  foreign key(user_email)   references "user"(email) on delete cascade,
  foreign key(problem_id)   references "problem"(id) on delete cascade);



# --- !Downs
drop table if exists "problem";
drop table if exists "user";
drop table if exists "user_solution";



