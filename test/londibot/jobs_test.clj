(ns londibot.jobs-test
  (:require [clojure.test :refer :all]
            [clojure.string :as str]
            [londibot.core.jobs :as j]
            [londibot.core.messages :as msg]
            [londibot.core.database :as db]))

(defn send-stub
  ([id text] (str id " / " text))
  ([text] text))

(defn schedule-stub [job send-stub]
  (send-stub "TEXT"))

(deftest test-job
  (testing "Applies natural language filter to cron upon job creation."
    (let [job (j/job "123" "Every MON at 18.00h" "slack")
          expected (db/new-job "123" "0 00 18 ? * MON" "slack")]
      (is (= expected job))))
  (testing "If the cron is non-parseable, it doesn't apply natural language filter."
    (let [job (j/job "123" "asdfghjkl" "slack")
          expected (db/new-job "123" "asdfghjkl" "slack")]
      (is (= expected job)))))

(deftest test-send-confirmation
  (testing "The user is confirmed that the scheduled notification has been saved succesfully."
    (let [service "some-service"
          confirmation-message (msg/scheduled-notification-confirmation "* 15 00 * ? *")
          job (j/job 123 "* 15 00 * ? *" service)]
      (is (str/includes? (j/send-confirmation job send-stub) confirmation-message)))))

(deftest test-schedule-all
  (testing "Schedules all the existing jobs in the database."
    (db/clean)
    (let [service "some-service"
          status-message1 "123 / TEXT"
          status-message2 "456 / TEXT"
          job1 (db/create (j/job 123 "* * * * * *" service))
          job2 (db/create (j/job 456 "* * * * * *" service))]
      (let [results (j/schedule-all service send-stub schedule-stub)]
        (is (= (first results) status-message1))
        (is (= (second results) status-message2))))))
