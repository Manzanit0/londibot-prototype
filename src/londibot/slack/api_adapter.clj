(ns londibot.slack.api-adapter
  (:require [londibot.core.api :as api]
            [clj-http.client :as http]
            [environ.core :refer [env]]
            [clojure.data.json :as json]))

(def token (env :slack-token))
(def slack-service-name "slack")

(defn post-message [channel-id msg]
  ; Sends a message to the input slack channel.
  (http/post (str
               "https://slack.com/api/chat.postMessage"
               "?token=" token
               "&channel=" channel-id
               "&text=" msg)))

(defn build-body [msg]
  ; Generates responses visible to everyone in the channel by default.
  {:text msg :response_type "in_channel"})

(defn get-status-notification []
  (-> (api/get-status-notification)
      (build-body)
      (json/write-str)))

(defn schedule-notification [request]
  (future
    (let [channel-id (get (:params request) "channel_id")
          cron-expr (get (:params request) "text")]
      (let [job (api/new-job channel-id cron-expr slack-service-name)]
        (api/create-scheduled-status-notification job (fn [msg] (post-message channel-id msg))))))
  ; Return empty body, otherwise Slack makes the user's command ephemeral (hides it).
  (-> ""
      (build-body)
      (json/write-str)))


(defn schedule-all-notifications []
  (api/schedule-all-notifications slack-service-name post-message))
