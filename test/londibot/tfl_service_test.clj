(ns londibot.tfl-service-test
  (:require [clojure.test :refer :all]
            [clojure.string :as str]
            [londibot.core.tfl-service :refer :all]))

; N.B. These tests are somewhat "integration tests" -> They are calling TFL API.

(defn send-stub
  ([id text] (str id " / " text))
  ([text] text))

(deftest send-tfl-status
  (let [tfl-status (send-tfl-status-message send-stub)]
    (testing "has correct format"
      (is (str/includes? tfl-status "The current status for London's tube is:\n\n")))
    (testing "contains information of random tube lines."
      (is (str/includes? tfl-status "Bakerloo"))
      (is (str/includes? tfl-status "Victoria"))
      (is (str/includes? tfl-status "Overground")))))

