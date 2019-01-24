(ns londibot.api-test
  (:require [clojure.test :refer :all]
            [clojure.string :as str]
            [londibot.api :as bot]
            [londibot.messages :as m]))

; Stub send function.
(defn send-fn [text] text)

(deftest test-status-notification
  (testing "sends a status report with latest tube information via the specified channel."
    (let [status-message "The current status for London's tube is:\n\n"]
      (is (str/includes? (bot/send-status-notification send-fn) status-message)))))
