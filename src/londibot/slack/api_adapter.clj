(ns londibot.slack.api-adapter
  (:require [londibot.core.api :as api]
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

(defn- async-create-scheduled-status-notification [request]
  (future
    (let [channel-id (get-channel-id request)
          cron-expr (get-text request)
          job (api/new-job channel-id cron-expr slack-service-name)]
      (api/create-scheduled-status-notification job (fn [msg] (post-message channel-id msg))))))

(defn get-status-notification []
  (-> (api/get-status-notification)
      (build-body)))

(defn schedule-notification [request]
  (async-create-scheduled-status-notification request)
  ; Return empty body, otherwise Slack makes the user's command ephemeral (hides it).
  (-> ""
      (build-body)))

(defn schedule-all-notifications []
  (api/schedule-all-notifications slack-service-name post-message))


(defn help [request]
  (-> request
      (get-text)
      (api/help)
      (build-body)))
