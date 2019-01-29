(ns londibot.core.tfl
  (:require [clj-http.client :as client]
            [clojure.xml :as xml]
            [clojure.zip :as zip]))

(defn find-tag [tagName, tags]
  (first (filter (fn [t] (= tagName (:tag t))) tags)))

(defn get-line [tags]
  (:Name (:attrs (find-tag :Line tags))))

(defn get-status [tags]
  (:Description (:attrs (find-tag :Status tags))))

(defn get-line-status [rawStatus]
  (let [tags (:content rawStatus)]
    {:line (get-line tags),
     :status (get-status tags)}))

(defn extract-data [content]
  (map get-line-status (:content content)))

(defn parse-xml [s]
  (xml/parse (java.io.ByteArrayInputStream. (.getBytes s))))

(defn tube-status []
  (extract-data (parse-xml (:body (client/get "http://cloud.tfl.gov.uk/TrackerNet/LineStatus")))))
