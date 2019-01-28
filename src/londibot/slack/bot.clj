(ns londibot.slack.bot
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.logger :refer :all]
            [ring.server.standalone :refer :all]
            [clojure.data.json :as json]
            [environ.core :refer [env]]
            [londibot.core.messages :refer :all]
            [londibot.core.tfl :refer :all])
  (:gen-class))

(def connection {:api-url "https://slack.com/api" :token (env :slack-token)})

(defn wrap-with-header [handler k v]
  (fn [request]
    (let [response (handler request)]
      (assoc-in response [:headers k] v))))

(defn print-response-stdout [handler]
  (fn [request]
    (let [response (handler request)]
      (println response) response)))

(defn generate-response [body]
  ; Generates responses visible to everyone in the channel by default.
  (json/write-str {:text body :response_type "in_channel"}))

(defroutes main-routes
  (POST "/tube-status" [] (generate-response (tube-status-message (tube-status))))
  (route/not-found "Page not found"))

(def app
  (-> main-routes
      (wrap-with-header "Content-Type" "application/json")
      (wrap-with-logger)
      (print-response-stdout)))

(defn -main
  [& args]
  (serve app {:port 5000 :open-browser? false}))
