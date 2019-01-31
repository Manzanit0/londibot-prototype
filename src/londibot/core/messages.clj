(ns londibot.core.messages
  (:require [clojure.string :as string]))

(defn pretty-print-line-status [line-status]
  (str "*" (:line line-status) "*" ": " (:status line-status)))

(defn tube-status-message [lines-statuses]
  (str "The current status for London's tube is:\n\n"
    (string/join "\n" (map pretty-print-line-status lines-statuses))))

(defn scheduled-notification-confirmation [expression]
  (str "I have succesfully scheduled a notification for you under the cron: `" expression "`"))

(defn schedule-help-message []
  (str "In order to schedule any notifications, "
       "make sure to use some of the following formats:"
       "\n\n - `Every MON at 08.00h`"
       "\n - `Every MON,TUE,WED at 21.00h`"
       "\n - `Every MON-FRI at 05:10PM`"
       "\n - `Every THU at 07:00AM`"
       "\n\n As you can see I understand both 12h and 24h formats, "
       "but I'm still learning how to express weekdays properly..."))
