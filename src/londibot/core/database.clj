(ns londibot.core.database
  (:require [environ.core :refer [env]])
  (:require [clojure.java.jdbc :as jdbc]))

(def db {:connection-uri (env :database-uri)})

(defn all
  ([]
   (jdbc/query db ["SELECT * FROM jobs"]))
  ([service]
   (jdbc/query db ["SELECT * FROM jobs WHERE service = ?" service])))

(defn fetch [id]
  (first (jdbc/query db ["SELECT * FROM jobs WHERE id = ?" id])))

(defn create [job]
  (first (jdbc/insert! db :jobs job)))

(defn update [id job]
  (first (jdbc/update! db :jobs job ["id = ?" id])))

(defn delete [id]
  (first (jdbc/delete! db :jobs ["id =  ?" id])))

(defn clean []
  (jdbc/delete! db :jobs []))

; Database model
(defn new-job [userid cronexpression service]
   {:userid userid :cronexpression cronexpression :service service})

(defn get-cron-expr [job]
  (:cronexpression job))

(defn get-user-id [job]
  (:userid job))

