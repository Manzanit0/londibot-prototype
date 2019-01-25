(ns londibot.database-test
  (:require [clojure.test :refer :all]
            [londibot.core.database :as db]))

;; Model tests

(deftest test-create-job
  (testing "Creates a job structure"
    (is (= {:userid 123 :cronexpression "123"} (db/new-job 123 "123")))))

(deftest test-get-cron
  (testing "Retrieves the cron expression from the job structure."
    (let [job (db/new-job 987 "123")]
      (is (= "123" (db/get-cron-expr job))))))

(deftest test-get-userid
  (testing "Retrieves the cron expression from the job structure."
    (let [job (db/new-job 987 "123")]
      (is (= 987 (db/get-user-id job))))))

;; Database tests

(deftest test-insert 
  (testing "inserts a single job to the DB."
    (let [record (db/new-job 12345 "* * * * * *")]
      (is (contains? (db/create record) :id)))))

(deftest test-fetch
  (testing "Fetches an existing record from the DB."
    (let [record (db/new-job 12345 "* * * * * *")]
      (let [existing-record (db/create record)]
        (let [id (:id existing-record)]
          (is (contains? (db/fetch id) :id)))))))

(deftest test-update
  (testing "Updates a job with new values."
    (let [record (db/new-job 12345 "* * * * * *")]
      (let [existing-record (db/create record)]
        (let [id (:id existing-record)]
          (db/update id (db/new-job 54321 "1 2 * 3 5"))
          (is (= 54321 (:userid (db/fetch id)))))))))

(deftest test-delete
  (testing "Deletes a record in the DB."
    (let [record (db/new-job 12345 "* * * * * *")]
      (let [existing-record (db/create record)]
        (let [id (:id existing-record)]
          (is (= 1 (db/delete id))))))))

(defn setup []
  (db/clean))

(defn teardown []
  (db/clean))

(defn once-fixture [f]
  (setup)
  (f)
  (teardown))

(use-fixtures :once once-fixture)
