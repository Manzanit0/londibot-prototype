(ns londibot.slack.api-adapter
  (:require [londibot.core.tfl-service :refer [tfl-status-message]]
            [londibot.core.jobs :as j]
            [londibot.core.messages :as msg]
            [clj-http.client :as http]
            [environ.core :refer [env]]
            [clojure.data.json :as json]))

(def token (env :slack-token))
(def slack-service-name "slack")

(defn- post-message [channel-id msg]
  (http/post (str
               "https://slack.com/api/chat.postMessage"
               "?token=" token
               "&channel=" channel-id
               "&text=" msg)))

(defn- build-body [msg]
  ; Generates responses visible to everyone in the channel by default.
  (-> {:text msg :response_type "in_channel"}
      (json/write-str)))

(defn- get-params [request]
  (:params request))

(defn- get-channel-id [request]
  (-> request
      (get-params)
      (get "channel_id")))

(defn- get-text [request]
  (-> request
      (get-params)
      (get "text")))

(defn- job [{{expr "text" channel-id "channel_id"} :params}]
  (j/job channel-id expr slack-service-name))

(defn- create-n-schedule [request]
  (let [channel-id (get-channel-id request)
        notify (fn [msg] (post-message channel-id msg))]
    (-> request
        (job)
        (j/create-n-schedule notify))))

(defn- async-create-n-schedule [request]
  (future (create-n-schedule request)))

(defn get-status-notification []
  (-> (tfl-status-message)
      (build-body)))

(defn schedule-notification [request]
  (async-create-n-schedule request)
  ; Return empty body, otherwise Slack makes the user's command ephemeral (hides it).
  (-> ""
      (build-body)))

(defn schedule-all-notifications []
  (j/schedule-all slack-service-name post-message))

(defn help [request]
  (-> request
      (get-text)
      (msg/help)
      (build-body)))
