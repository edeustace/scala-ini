# Introduction


Source code for [scalapuzzles.org](http://scalapuzzles.org) - inspired by 4clojure.org, but with a different ui and for Scala.

# Installation

Install [Play20](https://github.com/playframework/Play20)

Install [PostgreSql](http://www.postgresql.org/)

You will need to create a user and database for the tests and for the normal app. For test create a user: ````scalaini:hello```` and a 
db called ````scalaini_test````


The db for the regular app you configure through an environment variable ````DATABASE_URL````. 
So set that to whatever you want, here's an example ````export DATABASE_URL="postgres://db_username:my_password@localhost/scalaini"````

Then

    git clone git@github.com:edeustace/scala-ini.git
    cd scala-ini
    play run






