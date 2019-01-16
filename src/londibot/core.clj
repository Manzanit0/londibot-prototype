(ns londibot.core
  (:require [clojure.core.async :refer [<!!]]
            [clojure.string :as str]
            [environ.core :refer [env]]
            [morse.handlers :as h]
            [morse.polling :as p]
            [morse.api :as t]
            [londibot.tfl :as tfl]
            [londibot.messages :as msg])
  (:gen-class))

; TODO: fill correct token
(def token (env :telegram-token))


(h/defhandler handler

  (h/command-fn "start"
    (fn [{{id :id :as chat} :chat}]
      (t/send-text token id "Welcome to londibot!")))

  (h/command-fn "help"
    (fn [{{id :id :as chat} :chat}]
      (t/send-text token id "Help is on the way")))

  (h/command-fn "status"
    (fn [{{id :id :as chat} :chat}]
      (t/send-text 
        token id {:parse_mode "Markdown"} 
        (msg/tube-status-message (tfl/tube-status)))))

  (h/message-fn
    (fn [{{id :id} :chat :as message}]
      (t/send-text token id "I don't do a whole lot ... yet."))))


(defn -main
  [& args]
  (when (str/blank? token)
    (println "Please provde token in TELEGRAM_TOKEN environment variable!")
    (System/exit 1))

  (println "Starting the londibot")
  (<!! (p/start token handler)))
