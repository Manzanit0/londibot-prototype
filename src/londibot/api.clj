(ns londibot.api
  (:require [immutant.scheduling :refer :all]
            [londibot.tfl :as tfl]
            [londibot.messages :as msg]
            [londibot.database :as db]))

(defn create-job [userid cronexpression]
  {:userid userid :cronexpression cronexpression})

(defn get-cron-expr [job]
  (:cronexpression job))

(defn get-user-id [job]
  (:userid job))

(defn send-status-notification [send-fn]
  (send-fn (msg/tube-status-message (tfl/tube-status))))

(defn send-schedule-confirmation [job send-fn]
  (let [expr (get-cron-expr job)]
    (send-fn (msg/scheduled-notification-confirmation expr))))

(defn schedule-job [job send-fn]
  (schedule #(send-status-notification send-fn) (cron (get-cron-expr job))))

(defn create-scheduled-status-notification [job send-fn]
  (schedule-job job send-fn)
  (db/create job)
  (send-schedule-confirmation job send-fn))

(defn schedule-all-notifications [send-fn]
  ; N.B: send-fn will not have closed within the userId!
  (let [jobs (db/all)]
    (doseq [j jobs] ((fn [job] (schedule-job job (fn [text] (send-fn (get-user-id job) text)))) j))))
