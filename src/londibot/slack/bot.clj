(ns londibot.slack.bot
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [clojure.string :as str]
            [ring.middleware.params :refer :all]
            [ring.logger :refer :all]
            [ring.server.standalone :refer :all]
            [londibot.slack.middleware :as mdw]
            [londibot.slack.api-adapter :as api])
  (:gen-class))

(defroutes main-routes
  (POST "/tube-status" [] (api/get-status-notification))
  (POST "/schedule" request (api/schedule-notification request))
  (POST "/help"     request (api/help request))
  (route/not-found "Endpoint not found"))

(def app
  (-> main-routes
      (wrap-params) ; Slack sends channel data as url-endcoded-params.
      (mdw/wrap-with-header "Content-Type" "application/json")
      (mdw/print-response-stdout)
      (wrap-with-logger)))

(defn -main
  [& args]
  (when (str/blank? api/token)
    (println "Please provide token in SLACK_TOKEN environment variable!")
    (System/exit 1))

  (api/schedule-all-notifications)
  (serve app {:port 5000 :open-browser? false}))
