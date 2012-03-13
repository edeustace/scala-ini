# Introduction
Source code for a scala puzzle site - inspired by 4clojure.org, but with a different ui and for Scala.

# Installation

Install [Play20](https://github.com/playframework/Play20)

Then

    git clone git@github.com:edeustace/scala-ini.git
    cd scala-ini
    play run


# Todo
* dev:
 - when testing user should migrate the test db beforehand, when the tests are run the db should be reseeded
 - when developing the db should be migrated and reseeded before the server is run
 - when live to perform a migration the db should be backed up, migrated, any additional data should be seeded then.

the migrations/evolutions should only contain schema changes
there should be a separate mechanism for populating the db

play
db:test:prepare (custom command)
test (before each test have the db reset, run some scala code to add the data)

play
db:prepare (custom command)
db:seed (custom command)
run (disable evolutions)



