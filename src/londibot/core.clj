(ns londibot.core
  (:require [clojure.core.async :refer [<!!]]
            [clojure.string :as str]
            [environ.core :refer [env]]
            [morse.handlers :as h]
            [morse.polling :as p]
            [morse.api :as t]
            [londibot.api :as bot])
  (:gen-class))

(def token (env :telegram-token))

(defn send-message [id msg]
  (t/send-text token id {:parse_mode "Markdown"} msg))

(h/defhandler handler
  (h/command-fn
   "start"
   (fn
     [{{id :id, name :first_name} :chat}]
     (send-message id (str "Hi " name "! Welcome to londibot! I am your humble TFL services servant :)"))))

  (h/command-fn
   "help"
   (fn
     [{{id :id} :chat}]
     (send-message id "Right now the only available command is `/status`.")))

  (h/command-fn
   "status"
   (fn
     [{{id :id} :chat}]
     (bot/status-notification (fn [text] (send-message id text)))))

  (h/command-fn
   "schedule"
   (fn
     [{{id :id} :chat, cron-expr :text}]
     (bot/scheduled-status-notification (subs cron-expr 9) (fn [reply] (send-message id reply))))) ; We want to trim the "/schedule" command from the string.

  (h/message-fn
   (fn
     [{{id :id} :chat}]
     (send-message id "To see what I can do for you, use the `/help` command."))))


(defn -main
  [& args]
  (when (str/blank? token)
    (println "Please provide token in TELEGRAM_TOKEN environment variable!")
    (System/exit 1))

  (println "Starting the londibot")
  (<!! (p/start token handler)))
