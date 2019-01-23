(ns londibot.api
  (:require [immutant.scheduling :refer :all]
            [londibot.tfl :as tfl]
            [londibot.messages :as msg]))

(defn status-notification [send-fn]
  ; Sends a telegram message with the current status of the Tube.
  (apply send-fn [(msg/tube-status-message (tfl/tube-status))]))

(defn scheduled-status-notification [expression send-fn]
  ; Schedules a telegram message with the status of the Tube at that moment.
  (schedule #(status-notification send-fn) (cron expression)))