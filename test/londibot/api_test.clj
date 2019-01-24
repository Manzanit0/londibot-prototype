(ns londibot.api-test
  (:require [clojure.test :refer :all]
            [clojure.string :as str]
            [londibot.api :as bot]
            [londibot.messages :as m]))

(deftest test-create-job
  (testing "Creates a job structure"
    (is (= {:userid 123 :cronexpression "123"} (bot/create-job 123 "123")))))

(deftest test-get-cron
  (testing "Retrieves the cron expression from the job structure."
    (let [job (bot/create-job 987 "123")]
      (is (= "123" (bot/get-cron-expr job))))))

(deftest test-get-userid
  (testing "Retrieves the cron expression from the job structure."
    (let [job (bot/create-job 987 "123")]
      (is (= 987 (bot/get-user-id job))))))

; Stub send function.
(defn send-fn [text] text)

(deftest test-status-notification
  (testing "sends a status report with latest tube information via the specified channel."
    (let [status-message "The current status for London's tube is:\n\n"]
      (is (str/includes? (bot/send-status-notification send-fn) status-message)))))
