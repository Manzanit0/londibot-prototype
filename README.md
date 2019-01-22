# LondiBot

Telegram bot which reports the status of London TFL services.

This project was started as an exercise to get in touch with Clojure and its ecosystem.

## Getting started

First make sure you have leiningen installed, otherwise visit leiningen.org.

Secondly, in order to be able to run the bot you will need a PostgreSQL database which contains a `jobs` table. To install PostgreSQL visit https://www.postgresql.org/download/, and to replicate the jobs table, take the following script:

```sql
CREATE TABLE jobs (
  id serial PRIMARY KEY, 
  userId INT, 
  cronExpression VARCHAR(50), 
  submittedDate TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
```

To clone the database for the testing environment you might find this script handy:

```SQL
CREATE DATABASE londibot_test WITH TEMPLATE londibot OWNER your_user;
```

You can name the database as you wish, but make sure that the connection strings are properly configured in the `:profiles` section of leiningen's project map (`project.clj`). In this repository the DB's are set as `londibot` and `londibot_test`.

Lastly, once you have the database set up and leiningen install, simply run:

```
export TELEGRAM_TOKEN=<your_token>
lein run
```

And that should get you started.

## TFL API

In order to make this bot work it uses the current TFL Unified API. For more information, visit: https://api.tfl.gov.uk/.

## Documentation/Where I learnt

1. [How to set up environment profiles](https://8thlight.com/blog/kevin-buchanan/2014/12/08/organizing-your-clojure-environment-and-logs-with-leiningen.html)

2. [Using JDBC Clojure wrapper](http://clojure-doc.org/articles/ecosystem/java_jdbc/using_sql.html)

3. [Setting up testing fixtures](http://thornydev.blogspot.com/2012/09/before-and-after-logic-in-clojuretest.html)

## License

Copyright © 2019 Manzanit0

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
