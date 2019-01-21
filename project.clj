(defproject londibot "0.1.0-SNAPSHOT"
  :description "LondiBot: Telegram bot for TFL services"

  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :dependencies [[org.clojure/clojure "1.8.0"]
                 [environ             "1.1.0"]
                 [morse               "0.2.4"]
                 [clj-http            "3.9.1"]
                 [org.clojure/java.jdbc "0.7.8"]
                 [org.postgresql/postgresql "9.4-1201-jdbc41"]]

  :plugins [[lein-environ "1.1.0"]]

  :main ^:skip-aot londibot.core
  :target-path "target/%s"

  :profiles {:uberjar {:aot :all}})
