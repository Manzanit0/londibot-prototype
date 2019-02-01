(ns londibot.core.api
  (:require [clojure.core.match :refer [match]]
            [immutant.scheduling :refer :all]
            [londibot.core.tfl :as tfl]
            [londibot.core.messages :as msg]
            [londibot.core.database :as db]
            [londibot.core.natural-language :as nl]))

(defn new-job [id expr service]
  (let [cron (nl/to-cron expr)]
    (db/new-job id cron service)))

(defn get-status-notification []
  (msg/tube-status-message (tfl/tube-status)))

(defn send-status-notification [send-fn]
  (send-fn (get-status-notification)))

(defn send-schedule-confirmation [job send-fn]
  (let [expr (db/get-cron-expr job)]
    (send-fn (msg/scheduled-notification-confirmation expr))))

(defn schedule-job [job send-fn]
  (schedule #(send-status-notification send-fn) (cron (db/get-cron-expr job))))

(defn create-scheduled-status-notification [job send-fn]
  (future
    (schedule-job job send-fn)
    (db/create job)
    (send-schedule-confirmation job send-fn)))

(defn schedule-all-notifications
  ([service send-fn] ; Default the scheduling library method.
   (schedule-all-notifications service send-fn schedule-job))

  ([service send-fn schedule-fn]
   (->> (db/all service)
        (map (fn [job] (schedule-fn job (fn [text] (send-fn (db/get-user-id job) text)))))
        (doall))))

(defn help [topic]
  (match topic
        "schedule" (msg/schedule-help-message)
        :else "Try with the topic `schedule`, for example :)"))
