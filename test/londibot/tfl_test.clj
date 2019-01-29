(ns londibot.tfl-test
  (:require [clojure.test :refer :all]
            [londibot.core.tfl :as tfl]))

(def tfl-data 
"<ArrayOfLineStatus xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance' xmlns:xsd='http://www.w3.org/2001/XMLSchema' xmlns='http://webservices.lul.co.uk/'>
  <LineStatus ID='0' StatusDetails='Severe delays due to an earlier customer incident. Tickets accepted on Local London Buses.'>
    <BranchDisruptions/>
    <Line ID='1' Name='Bakerloo'/>
    <Status ID='SD' CssClass='DisruptedService' Description='Severe Delays' IsActive='true'>
    <StatusType ID='1' Description='Line'/>
    </Status>
  </LineStatus>
  <LineStatus ID='0' StatusDetails=''>
    <BranchDisruptions/>
    <Line ID='10' Name='Circle'/>
    <Status ID='SD' CssClass='GoodService' Description='Good Service' IsActive='true'>
    <StatusType ID='1' Description='Line'/>
    </Status>
  </LineStatus>
</ArrayOfLineStatus>")

(def tfl-data-map
  (#'tfl/parse-xml tfl-data))

(def final-data-form
  (list {:line "Bakerloo" :status "Severe Delays"} {:line "Circle" :status "Good Service"}))

(deftest extract-data-test
  (testing "parses xml to a map"
    (is (= final-data-form (tfl/extract-data tfl-data-map)))))

(deftest test-tfl-api-model
  (testing "the model returned by the TFL API is valid and correctly parsed."
    (let [data (tfl/tube-status)]
      (is (= (count data) 15)) ; 11 tube lines + 4.
      (is (every? #(contains? % :line) data))
      (is (every? #(contains? % :status) data)))))
