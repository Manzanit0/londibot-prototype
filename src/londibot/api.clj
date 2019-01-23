(ns londibot.api
  (:require [immutant.scheduling :refer :all]
            [londibot.tfl :as tfl]
            [londibot.messages :as msg]
            [londibot.database :as db]))

(defn status-notification [send-fn]
  (apply send-fn [(msg/tube-status-message (tfl/tube-status))]))

(defn scheduled-status-notification [expression send-fn]
  (schedule #(status-notification send-fn) (cron expression))
  ;(db/create job)) ; TODO persist the job.
  (let [confirmation (msg/scheduled-notification-confirmation expression)]
    (apply send-fn [confirmation])))
