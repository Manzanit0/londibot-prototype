(ns londibot.core.scheduling
  (:require [immutant.scheduling :as sc]
            [londibot.core.database :as db]))

; Reference:
; http://immutant.org/documentation/2.1.10/apidoc/guide-scheduling.html

(defn- to-options [job]
  (let [c (db/get-cron-expr job)
        i (db/get-id job)]
    (or i
      (throw (Exception. "Job has no Id.")))
    (-> {}
        (cond->
          c (assoc :cron c)
          i (assoc :id i)))))

(defn schedule [action job]
  (->> job
       (to-options)
       (sc/schedule action)))

(defn unschedule [job]
  (-> job
      (to-options)
      (sc/stop)))
