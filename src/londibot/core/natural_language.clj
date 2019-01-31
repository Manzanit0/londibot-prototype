(ns londibot.core.natural-language)

(def ^:private pattern
  #"^.*Every (?<day>.*?) at (?<hour>.*?)[\.|:](?<minutes>.*?)(?<modifier>h|AM|PM).*$")

(defn- increment-by-12 [hour modifier]
  (+ hour (if (= modifier "PM") 12 0)))

(defn- reduce-by-24 [hour]
  (- hour (if (>= hour 24) 24 0)))

(defn- prepend-zero [hour]
  (if (= (count hour) 1) (str "0" hour) hour))

(defn- to-24-format [hour modifier]
    (-> hour
      (Integer/parseInt)
      (increment-by-12 modifier)
      (reduce-by-24)
      (str)
      (prepend-zero)))

(defn- build-cron
  ([match]
   (let [[m days hour minutes modifier] match]
     (build-cron days (to-24-format hour modifier) minutes)))
  ([days hour minute]
   (str "0 " minute " " hour " ? * " days)))

(defn to-cron [expression]
  ; Captures expressions such as: Every MON-FRI at 18:00h and transforms them to a cron expression.
  ; Times can be in 12h and 24h format.
  ; Days must be in cron format (MON-FRI, TUE...)
  ; Upon unmatch (nil), returns the input expression.
  (let [match (re-matches pattern expression)]
    (if match (build-cron match) expression)))
