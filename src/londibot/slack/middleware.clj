(ns londibot.slack.middleware)

(defn wrap-with-header [handler k v]
  (fn [request]
    (let [response (handler request)]
      (assoc-in response [:headers k] v))))

(defn print-response-stdout [handler]
  (fn [request]
    (let [response (handler request)]
      (println response) response)))
