(ns londibot.api
  (:require [immutant.scheduling :refer :all]
            [londibot.tfl :as tfl]
            [londibot.messages :as msg]
            [londibot.database :as db]))

(defn send-status-notification [send-fn]
  (send-fn (msg/tube-status-message (tfl/tube-status))))

(defn send-schedule-confirmation [job send-fn]
  (let [expr (db/get-cron-expr job)]
    (send-fn (msg/scheduled-notification-confirmation expr))))

(defn schedule-job [job send-fn]
  (schedule #(send-status-notification send-fn) (cron (db/get-cron-expr job))))

(defn create-scheduled-status-notification [job send-fn]
  (schedule-job job send-fn)
  (db/create job)
  (send-schedule-confirmation job send-fn))

(defn schedule-all-notifications [send-fn]
  ; N.B: send-fn will not have closed within the userId!
  (let [jobs (db/all)]
    (doseq [j jobs] ((fn [job] (schedule-job job (fn [text] (send-fn (db/get-user-id job) text)))) j))))
