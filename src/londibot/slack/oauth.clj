(ns londibot.slack.oauth
  (:require [clj-http.client :as http]
            [environ.core :refer [env]]))

(def client-id (env :slack-client-id))
(def client-secret (env :slack-client-secret))

(defn- authorize [code]
  (http/get (str
               "https://slack.com/api/oauth.access"
               "?code=" code
               "&client_id=" client-id
               "&client_secret=" client-secret)))

(defn handle [request]
  (let [code (get (:params request) "code")]
    (if code
      (authorize code)
      (println "Missing OAuth code!"))))
