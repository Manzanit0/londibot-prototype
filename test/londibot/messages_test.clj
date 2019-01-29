(ns londibot.messages-test
  (:require [clojure.test :refer :all]
            [londibot.core.messages :as msg]))

(def line-status 
  (list {:line "Bakerloo" :status "Severe Delays"} {:line "Circle" :status "Good Service"}))

(deftest print-single-line-status
  (testing "prints the status of a single line with Markdown"
    (is (= "*Bakerloo*: Severe Delays"
          (msg/pretty-print-line-status (nth line-status 0))))))

(def status-message "The current status for London's tube is:\n\n*Bakerloo*: Severe Delays\n*Circle*: Good Service")

(deftest print-all-lines-status
  (testing "prints the status of all the lines with Markdown"
    (is (= status-message (msg/tube-status-message line-status)))))

(def confirmation-message "I have succesfully scheduled a notification for you under the cron: `* * * * * *`")

(deftest print-scheduled-confirmation
  (testing "prints a confirmation message for scheduled jobs."
    (is (= confirmation-message (msg/scheduled-notification-confirmation "* * * * * *")))))
