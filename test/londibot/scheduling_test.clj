(ns londibot.scheduling-test
  (:require [clojure.test :refer :all]
            [londibot.core.database :as db]
            [londibot.core.scheduling :as s]
            [immutant.util :as u]))

; Turn off Quartz logging during tests -> It's too noisy.
(u/set-log-level! (or (System/getenv "LOG_LEVEL") :OFF))

(def job-without-id (db/new-job "userid" "* * * ? * *" "service"))
(def job (assoc job-without-id :id "111"))

(deftest to-options
  (testing "a legit immutant 'options' map is built from a job structure."
    (is (= (#'s/to-options job) {:id "111", :cron "* * * ? * *"})))
  (testing "to-options throws exception upon a job without Id"
    (is (thrown? Exception (#'s/to-options job-without-id)))))

(deftest schedule
  (testing "The action is executed."
    (let [p (promise)]
      (s/schedule #(deliver p :success) job)
      (is (= :success (deref p 10000 :failure)))))
  (testing "Throws an exception if job doesn't have an Id"
    (is (thrown? Exception (s/schedule #(comment) job-without-id)))))

(deftest unschedule
  (testing "unschedules an existing scheduled job"
    (s/schedule #(comment) job)
    (is (s/unschedule job)))
  (testing "returns false if there is no job to unschedule"
    (is (not (s/unschedule {:id "unknown"})))))
