(ns londibot.core.tfl
  (:require [clj-http.client :as client]
            [clojure.xml :as xml]))

(defn- find-tag [tagName, tags]
  (->> tags
       (filter (fn [t] (= tagName (:tag t))))
       (first)))

(defn- get-line [tags]
  (->> tags
       (find-tag :Line)
       (:attrs)
       (:Name)))

(defn- get-status [tags]
  (->> tags
       (find-tag :Status)
       (:attrs)
       (:Description)))

(defn- get-line-status [rawStatus]
  (let [tags (:content rawStatus)]
    {:line (get-line tags),
     :status (get-status tags)}))

(defn- parse-xml [s]
  (->> s
       (.getBytes)
       (java.io.ByteArrayInputStream.)
       (xml/parse)))

(defn extract-data [content]
  (->> content
       (:content)
       (map get-line-status)))

(defn tube-status []
  (-> "http://cloud.tfl.gov.uk/TrackerNet/LineStatus"
      (client/get)
      (get :body)
      (parse-xml)
      (extract-data)))
