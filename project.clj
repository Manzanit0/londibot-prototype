(defproject londibot "0.1.0-SNAPSHOT"
  :description "LondiBot: Telegram bot for TFL services"

  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :dependencies [[org.clojure/clojure       "1.8.0"]
                 [org.clojure/core.match    "0.3.0-alpha5"]
                 [environ                   "1.1.0"]
                 [morse                     "0.2.4"] ; Telegram API specific
                 [clj-http                  "3.9.1"]
                 [compojure                 "1.6.1"]
                 [ring/ring-core            "1.7.1"]
                 [ring/ring-jetty-adapter   "1.7.1"]
                 [ring-logger               "1.0.1"]
                 [ring-server               "0.5.0"]
                 [org.clojure/data.json     "0.2.6"]
                 [org.immutant/scheduling   "2.1.10"]
                 [org.clojure/java.jdbc     "0.7.8"]
                 [org.postgresql/postgresql "9.4-1201-jdbc41"]]

  :min-lein-version "2.0.0"
  :plugins [[lein-environ "1.1.0"]]

  :main nil
  :target-path "target/%s"
  :aliases {"telegram"      ["run" "-m" "londibot.telegram.bot"]
            "slack"         ["run" "-m" "londibot.slack.bot"]}

  :profiles {:uberjar    {:aot :all}
             :dev        {:env {:clj-env :development
                                :database-url "jdbc:postgresql://localhost:5432/londibot"}}
             :test       {:env {:clj-env :test
                                :database-url "jdbc:postgresql://localhost:5432/londibot_test"}}
             :production {:env {:clj-env :production}}}) ; Environment URL set in environment ;)

