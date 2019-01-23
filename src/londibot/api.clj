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

(defn status-notification [send-fn]
  (apply send-fn [(msg/tube-status-message (tfl/tube-status))]))

(defn scheduled-status-notification [job send-fn]
  (let [expr (get-cron-expr job)]
    (schedule #(status-notification send-fn) (cron expr))
    (db/create job)
    (apply send-fn [(msg/scheduled-notification-confirmation expr)])))
