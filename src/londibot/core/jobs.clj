(ns londibot.core.jobs
  (:require [londibot.core.database :as db]
            [londibot.core.scheduling :as sc]
            [londibot.core.natural-language :as nl]
            [londibot.core.messages :as msg]))

(defn job [channelid expr service]
  (let [cron (nl/to-cron expr)]
    (db/new-job channelid cron service)))

(defn create [job]
  (db/create job))

(defn schedule [job action]
  (sc/schedule action job))

(defn send-confirmation [job send-fn]
  (-> job
      (db/get-cron-expr)
      (msg/scheduled-notification-confirmation)
      (send-fn)))

(defn create-n-schedule [job send-fn]
  (-> job
      (create)
      (schedule send-fn)
      (send-confirmation send-fn)))

(defn schedule-all
  ; Schedules all the jobs persisted in the database for a given service.
  ; This is usually called at the application startup.
  ([service send-fn]
   (schedule-all service send-fn schedule))
  ([service send-fn schedule-fn]
   (->> (db/all service)
        (map (fn [job] (schedule-fn job (fn [text] (send-fn (db/get-user-id job) text)))))
        (doall))))
