(ns londibot.slack.bot
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.adapter.jetty :refer :all]
            [environ.core :refer [env]]
            [londibot.core.messages :refer :all]
            [londibot.core.tfl :refer :all])
  (:gen-class))

(def connection {:api-url "https://slack.com/api" :token (env :slack-token)})

; FIXME - Configure logger in order to see if the header is set correctly.
(defn in-channel-visibility [handler]
  ; Makes the message visible to all the channel members.
  (fn [request]
    (let [response (handler request)]
      (assoc-in response [:headers "response_type"] "in_channel"))))

(defroutes main-routes
  (POST "/tube-status" [] (tube-status-message (tube-status)))
  (route/not-found "Page not found"))

(def app
  (-> main-routes
      (in-channel-visibility)))

(defn -main
  [& args]
  (run-jetty app {:port 5000}))
