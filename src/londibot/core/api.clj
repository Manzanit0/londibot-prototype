(ns londibot.core.api
  (:require [immutant.scheduling :refer :all]
            [londibot.core.tfl :as tfl]
            [londibot.core.messages :as msg]
            [londibot.core.database :as db]))

(defn new-job [id expr service] (db/new-job id expr service))

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

(defn schedule-all-notifications
  ([service send-fn] ; Default the scheduling library method.
   (schedule-all-notifications service send-fn schedule-job))

  ([service send-fn schedule-fn]
   (let [jobs (db/all service)]
     (println (str "INFO: Number scheduled jobs – " (count jobs)))
     ; Use doall + map vs doseq because we need the return values in order to test the code.
     (doall (map (fn [job] (schedule-fn job (fn [text] (send-fn (db/get-user-id job) text)))) jobs)))))
