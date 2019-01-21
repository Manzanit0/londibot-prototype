(ns londibot.database-test
  (:require [clojure.test :refer :all]
            [londibot.database :as db]))

(def record {:userId 12345 :cronExpression "* * * * *"})

(deftest test-insert 
  (testing "inserts a single job to the DB."
    (is (contains? (db/create record) :id))))

(deftest test-fetch
  (let [existing-record (db/create record)]
    (let [id (:id existing-record)]
      (testing "Fetches an existing record from the DB."
        (is (contains? (db/fetch id) :id))))))

(deftest test-update
  (testing "Updates a job with new values."
    (let [existing-record (db/create record)]
      (let [id (:id existing-record)]
        (db/update id {:userid 54321 :cronexpression "1 2 * 3 5"})
        (is (= 54321 (:userid (db/fetch id))))))))

(deftest test-delete
  (testing "Deletes a record in the DB."
    (let [existing-record (db/create record)]
      (let [id (:id existing-record)]
        (is (= 1 (db/delete id)))))))

; TODO - Take the creation of records to the setup
(defn setup []
  (println "setup"))

(defn teardown []
  (println "teardown"))

(defn once-fixture [f]
  (setup)
  (f)
  (teardown))

(use-fixtures :once once-fixture)
