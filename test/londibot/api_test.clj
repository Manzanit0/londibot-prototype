(ns londibot.api-test
  (:require [clojure.test :refer :all]
            [clojure.string :as str]
            [londibot.core.api :as api]
            [londibot.core.database :as db]
            [londibot.core.messages :as msg]))

; Stub send function.
(defn send-fn
  ([id text] (str id " / " text))
  ([text] text))

(deftest test-status-notification
  (testing "sends a status report with latest tube information via the specified channel."
    (let [status-message "The current status for London's tube is:\n\n"]
      (is (str/includes? (api/send-status-notification send-fn) status-message)))))

(deftest test-schedule-confirmation
  (testing "The user is confirmed that the scheduled notification has been saved succesfully."
    (let [service "some-service"
          confirmation-message (msg/scheduled-notification-confirmation "* 15 00 * ? *")
          job (api/new-job 123 "* 15 00 * ? *" service)]
      (is (str/includes? (api/send-schedule-confirmation job send-fn) confirmation-message)))))

; Stub the schedule function.
(defn schedule-fn [job send-fn]
  (send-fn "TEXT"))

(deftest test-schedule-all-notifications
  (testing "Schedules all the existing jobs in the database."
    (db/clean)
    (let [service "some-service"
          status-message1 "123 / TEXT"
          status-message2 "456 / TEXT"
          job1 (db/create (api/new-job 123 "* * * * * *" service))
          job2 (db/create (api/new-job 456 "* * * * * *" service))]
      (let [results (api/schedule-all-notifications service send-fn schedule-fn)]
        (is (= (first results) status-message1))
        (is (= (second results) status-message2))))))
