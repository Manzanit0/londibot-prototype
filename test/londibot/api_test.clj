(ns londibot.api-test
  (:require [clojure.test :refer :all]
            [clojure.string :as str]
            [londibot.api :as bot]
            [londibot.database :as db]
            [londibot.messages :as msg]))

; Stub send function.
(defn send-fn
  ([id text] (str id " / " text))
  ([text] text))

(deftest test-status-notification
  (testing "sends a status report with latest tube information via the specified channel."
    (let [status-message "The current status for London's tube is:\n\n"]
      (is (str/includes? (bot/send-status-notification send-fn) status-message)))))

(deftest test-schedule-confirmation
  (testing "The user is confirmed that the scheduled notification has been saved succesfully."
    (let [confirmation-message (msg/scheduled-notification-confirmation "* 15 00 * ? *")
          job (db/new-job 123 "* 15 00 * ? *")]
      (is (str/includes? (bot/send-schedule-confirmation job send-fn) confirmation-message)))))

; Stub the schedule function.
(defn schedule-fn [job send-fn]
  (send-fn "TEXT"))

(deftest test-schedule-all-notifications
  (testing "Schedules all the existing jobs in the database."
    (db/clean)
    (let [status-message1 "123 / TEXT"
          status-message2 "456 / TEXT"
          job1 (db/create (db/new-job 123 "* * * * * *"))
          job2 (db/create (db/new-job 456 "* * * * * *"))]
      (let [results (bot/schedule-all-notifications send-fn schedule-fn)]
        (is (= (first results) status-message1))
        (is (= (second results) status-message2))))))
