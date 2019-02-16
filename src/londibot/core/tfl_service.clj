(ns londibot.core.tfl-service
  (:require [londibot.core.tfl :as tfl]
            [londibot.core.messages :as msg]))

(defn tfl-status-message []
  (-> (tfl/tube-status)
      (msg/tube-status-message)))

(defn send-tfl-status-message [send-fn]
  (-> (tfl-status-message)
      (send-fn)))

