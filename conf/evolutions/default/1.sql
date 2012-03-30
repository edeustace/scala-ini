# --- First database schema

# --- !Ups


create table app_user(
  email                     varchar(255) not null primary key,
  name                      varchar(255) not null,
  password                  varchar(255) not null);


create table puzzle (
  id                      serial,
  name                    varchar(255) not null,
  description             varchar(255) not null,
  body                    varchar(8000) not null,
  level                   varchar(255) not null,
  category                varchar(255) not null,
  user_email              varchar(255) not null,
  foreign key(user_email) references app_user (email) on delete cascade,
  constraint pk_puzzle   primary key (id));

create table user_solution(
  id                        serial primary key,
  user_email                varchar(255) not null,
  puzzle_id                bigint not null,
  solution                  varchar(8000) not null,
  foreign key(user_email)   references app_user (email) on delete cascade,
  foreign key(puzzle_id)   references puzzle (id) on delete cascade);

create table puzzle_url_key (
  id                        serial primary key,
  puzzle_id                 bigint not null,
  key                       varchar(8) not null,
  foreign key(puzzle_id) references puzzle (id));


# --- !Downs
drop table if exists puzzle;
drop table if exists app_user;
drop table if exists user_solution;
drop table if exists puzzle_url_key;



