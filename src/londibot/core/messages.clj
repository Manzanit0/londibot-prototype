(ns londibot.core.messages
  (:require [clojure.string :as string]))

(defn pretty-print-line-status [line-status]
  (str "*" (:line line-status) "*" ": " (:status line-status)))

(defn tube-status-message [lines-statuses]
  (str "The current status for London's tube is:\n\n"
    (string/join "\n" (map pretty-print-line-status lines-statuses))))

(defn scheduled-notification-confirmation [expression]
  (str "I have succesfully scheduled a notification for you under the cron: `" expression "`"))
