(ns londibot.database
  (:require [clojure.java.jdbc :as jdbc]))

(def db {:connection-uri "jdbc:postgresql://localhost:5432/londibot"})

(defn fetch [id]
  (first (jdbc/query db ["SELECT * FROM jobs WHERE id = ?" id])))

(defn create [job]
  (first (jdbc/insert! db :jobs job)))

(defn update [id job]
  (first (jdbc/update! db :jobs job ["id = ?" id])))

(defn delete [id]
  (first (jdbc/delete! db :jobs ["id =  ?" id])))
