(ns londibot.slack.bot
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.params :refer :all]
            [ring.logger :refer :all]
            [ring.server.standalone :refer :all]
            [clojure.data.json :as json]
            [clj-http.client :as http]
            [environ.core :refer [env]]
            [londibot.slack.middleware :as mdw]
            [londibot.core.api :as api]
            [londibot.core.messages :refer :all]
            [londibot.core.tfl :refer :all])
  (:gen-class))

(def token (env :slack-token))

(defn build-body [msg]
  ; Generates responses visible to everyone in the channel by default.
  {:text msg :response_type "in_channel"})

(defn post-message [channel-id msg]
  (http/post (str
               "https://slack.com/api/chat.postMessage"
               "?token=" token
               "&channel=" channel-id
               "&text=" msg)))

(defroutes main-routes
  (POST "/tube-status" []
        (-> (tube-status-message (tube-status))
            (build-body)
            (json/write-str)))

  ; FIXME - This will cause conflicts with Telegram UI.
  ; The DB model needs an update to specify which UI has requested what.
  ; Furthermore, the userId needs to be changed to varchar -> Slack uses strings.
  (POST "/schedule" request
        (let [channel-id (get (:params request) "channel_id")
              cron-expr (get (:params request) "text")]
          (let [job (api/new-job channel-id cron-expr)]
            (api/create-scheduled-status-notification job (fn [msg] (post-message channel-id msg)))))
        (-> "Job scheduled successfully"
            (build-body)
            (json/write-str)))

  (route/not-found "Page not found"))

(def app
  (-> main-routes
      (wrap-params) ; Slack sends channel data as url-endcoded-params.
      (mdw/wrap-with-header "Content-Type" "application/json")
      (mdw/print-response-stdout)
      (wrap-with-logger)))

(defn -main
  [& args]
  (serve app {:port 5000 :open-browser? false}))
