# LondiBot

Telegram/Slack bot which reports the status of London TFL services.

This project was started as an exercise to get in touch with Clojure and its ecosystem.

## Live demo

If you want to see how the bot works in Slack, simply add it to your workspace: [link](https://slack.com/oauth/authorize?client_id=2225972970.533137885794&scope=incoming-webhook,commands,bot,users:read,chat:write:bot,groups:read,channels:read,im:read,mpim:read,users:read.email,channels:write). A live version has been deployed to Heroku – be aware that it is using free dynos, so it make take time to awake it.

## Getting started

_**Disclaimer:** Before continuing with the below instructions I assume you are aquainted with either the Slack API or the Telegram API in order to develop bots. If that is not the case, please make sure you are familiar with them._

First make sure you have leiningen installed, otherwise visit leiningen.org.

Secondly, in order to be able to run any of the bots you will need a PostgreSQL database which contains a `jobs` table. To install PostgreSQL visit https://www.postgresql.org/download/, and to replicate the jobs table, take the following script:

```sql
CREATE TABLE jobs (
  id serial PRIMARY KEY, 
  userId VARCHAR(50), 
  cronExpression VARCHAR(50), 
  service VARCHAR(50), 
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
lein telegram
```

or, in case you prefer to run the Slack bot:

```
export SLACK_TOKEN=<your_token>
lein slack
```

And that should get you started.

## Explanation

As you can see in the code, this repository has 3 main sections: the first one is the core functionality to interact with the TFL API. It is a set of Clojure functions which retrieve, parse and expose the status of the tube in London via the TFL API and also allow the application to save scheduled notifications.

The scheduled notifications or *jobs* represent future notifications which users can request. They are a combination of the `channelId` to which notifications will be sent, a `cron expression` which specifies the regularity of the notifications and the `service` or application through which the request has been made (that's Telegram or Slack). Whenever a user requests a scheduled notification of the status of the tube, a job will be saved in the database.

The other two sections of the repository are the platform-specific code for the bots; Telegram and Slack. In both of them a ring server is implemented to process requests and return TFL data. For this reason, there are two main functions, one for each of the bots – for this, two aliases have been set in the `project.clj` for convenience.

Its important to note that both bots work with the same database. That's why the `jobs` table has a `service` column. The column allows the applications to distinguish which scheduled jobs correspond to which users. Records will have the values of either `telegram` or `slack`. This allows them, in case the use both applications, to schedule different jobs from the different services.

## TFL API

In order to make this bot work it uses the current TFL Unified API. For more information, visit: https://api.tfl.gov.uk/.

## Documentation/Where I learnt

1. [How to set up environment profiles](https://8thlight.com/blog/kevin-buchanan/2014/12/08/organizing-your-clojure-environment-and-logs-with-leiningen.html)

2. [Using JDBC Clojure wrapper](http://clojure-doc.org/articles/ecosystem/java_jdbc/using_sql.html)

3. [Setting up testing fixtures](http://thornydev.blogspot.com/2012/09/before-and-after-logic-in-clojuretest.html)

4. [Regexes in Clojure](https://lispcast.com/clojure-regex/)

5. [The complete guide to Clojure destructuring](http://blog.brunobonacci.com/2014/11/16/clojure-complete-guide-to-destructuring)

## License

Copyright © 2019 Manzanit0

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
