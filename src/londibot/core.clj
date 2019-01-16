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
      (t/send-text
        token id
        "Welcome to londibot! I am your humble TFL services servant :)")))

  (h/command-fn "help"
    (fn [{{id :id :as chat} :chat}]
      (t/send-text
        token id {:parse_mode "Markdown"}
        "Right now the only available command is `/status`.")))

  (h/command-fn "status"
    (fn [{{id :id :as chat} :chat}]
      (t/send-text
        token id {:parse_mode "Markdown"}
        (msg/tube-status-message (tfl/tube-status)))))

  (h/message-fn
    (fn [{{id :id} :chat :as message}]
      (t/send-text
        token id {:parse_mode "Markdown"}
        "To see what I can do for you, use the `/help` command."))))


(defn -main
  [& args]
  (when (str/blank? token)
    (println "Please provde token in TELEGRAM_TOKEN environment variable!")
    (System/exit 1))

  (println "Starting the londibot")
  (<!! (p/start token handler)))
