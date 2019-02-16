(ns londibot.slack.api-adapter-test
  (:require [clojure.test :refer :all]
            [londibot.slack.api-adapter :as api]
            [londibot.core.jobs :as j]))

(deftest test-build-body
  (testing "the body built contains the correct in_channel property and is serialized to JSON."
    (let [expected-body "{\"text\":\"Hey!\",\"response_type\":\"in_channel\"}"]
      (is (= (#'api/build-body "Hey!") expected-body)))))

(deftest test-get-channel-id
  (testing "extracts the Channel Id from the request's params"
    (let [expected "12345"
          request {:body "body" :params {"channel_id" "12345"}}]
      (is (= (#'api/get-channel-id request) expected)))))

(deftest test-get-text
  (testing "extracts the text from the request's params"
    (let [expected "12345"
          request {:body "body" :params {"text" "12345"}}]
      (is (= (#'api/get-text request) expected)))))

(deftest test-job
  (testing "creates a job from the request's params"
    (let [request {:body "body" :params {"channel_id" "12345" "text" "cron*"}}
          job (#'api/job request)
          expected (j/job "12345" "cron*" "slack")]
      (is (= job expected)))))

