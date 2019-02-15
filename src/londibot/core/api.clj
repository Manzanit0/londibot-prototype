(ns londibot.core.api
  (:require [clojure.core.match :refer [match]]
            [londibot.core.scheduling :as sc]
            [londibot.core.tfl :as tfl]
            [londibot.core.messages :as msg]
            [londibot.core.database :as db]
            [londibot.core.natural-language :as nl]))

(defn new-job [id expr service]
  (let [cron (nl/to-cron expr)]
    (db/new-job id cron service)))

(defn get-status-notification []
  (-> (tfl/tube-status)
      (msg/tube-status-message)))

(defn send-status-notification [send-fn]
  (-> (get-status-notification)
      (send-fn)))

(defn send-schedule-confirmation [job send-fn]
  (-> job
      (db/get-cron-expr)
      (msg/scheduled-notification-confirmation)
      (send-fn)))

(defn schedule-job [job send-fn]
  (sc/schedule #(send-status-notification send-fn) job))

(defn create-scheduled-status-notification [job send-fn]
  (db/create job)
  (schedule-job job send-fn)
  (send-schedule-confirmation job send-fn))

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
         :else (msg/default-help-message)))
