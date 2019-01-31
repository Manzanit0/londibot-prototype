(ns londibot.natural-language-test
  (:require [clojure.test :refer :all]
            [londibot.core.natural-language :as nl]))

(deftest test-increment-by-12
  (testing "increments by 12 the hour if it's in 12h format with PM modifier"
    (is (= 9 (#'nl/increment-by-12 9 "AM")))
    (is (= 21 (#'nl/increment-by-12 9 "PM")))
    (is (= 12 (#'nl/increment-by-12 12 "AM")))
    (is (= 24 (#'nl/increment-by-12 12 "PM")))))

(deftest test-reduce-by-24
  (testing "reduces the hour by 24 if it's bigger than 24"
    (is (= 9 (#'nl/reduce-by-24 9)))
    (is (= 23 (#'nl/reduce-by-24 23)))
    (is (= 0 (#'nl/reduce-by-24 24)))
    (is (= 2 (#'nl/reduce-by-24 26)))))

(deftest test-prepend-zero
  (testing "prepends a zero to the hour expression in case it's a single digit"
   (is (= "01" (#'nl/prepend-zero "1")))
   (is (= "09" (#'nl/prepend-zero "9")))
   (is (= "21" (#'nl/prepend-zero "21")))))

(deftest test-to-24-format
  (testing "converts 12h format hours to 24h format hours"
   (is (= "09" (#'nl/to-24-format "9" "AM")))
   (is (= "21" (#'nl/to-24-format "09" "PM")))
   (is (= "00" (#'nl/to-24-format "12" "PM")))))

(deftest test-build-cron
  (testing "prepends a zero to the hour expression in case it's a single digit"
   (is (= "0 45 21 ? * MON-FRI" (#'nl/build-cron "MON-FRI" "21" "45")))
   (is (= "0 00 02 ? * TUE" (#'nl/build-cron "TUE" "02" "00")))
   (is (= "0 55 09 ? * *" (#'nl/build-cron "*" "09" "55")))))

(deftest test-to-cron
  (testing "transforms natural language expressions to cron"
    (is (= "0 45 21 ? * MON-FRI" (#'nl/to-cron "Every MON-FRI at 09.45PM")))
    (is (= "0 45 09 ? * MON-FRI" (#'nl/to-cron "Every MON-FRI at 09.45AM")))
    (is (= "0 00 02 ? * TUE"     (#'nl/to-cron "Every TUE at 02.00h")))
    (is (= "0 50 08 ? * WED-SUN" (#'nl/to-cron "Every WED-SUN at 8.50h")))
    (is (= "0 55 09 ? * *"       (#'nl/to-cron "Every * at 09:55AM")))))
