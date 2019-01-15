(ns londibot.messages
  (:require [londibot.tfl :refer :all]
            [clojure.string :as string]))

(defn pretty-print-line-status [line-status]
  (str "*" (:line line-status) "*" ": " (:status line-status)))

(defn tube-status-message []
  (str "The current status for London's tube is:\n\n"
    (string/join "\n" (map pretty-print-line-status (tube-status)))))
