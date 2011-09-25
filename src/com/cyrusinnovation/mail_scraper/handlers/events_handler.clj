(ns com.cyrusinnovation.mail-scraper.handlers.events-handler
  (:require [com.cyrusinnovation.mail-scraper.parsers.mime-message-parser :as parser])
  (:require [com.cyrusinnovation.mail-scraper.persistence.message-persistence :as persister])
	(:require [com.cyrusinnovation.mail-scraper.utils.template-utils :as utils])
  (:require [net.cgrand.enlive-html :as html]))

(defn mime-message-from [request]
    (-> (.getInputStream request) (parser/mime-message-from-stream)))

(defn save-incoming-message [message]
  (-> (parser/parse message) (persister/store)))

(defn show-user-confirmation [servlet]
  (let [template (utils/prepare-template servlet "events"
                                         [substitution-values]
                                         [:title] (html/content (:title substitution-values))
                                         [:h2.message] (html/content (:message substitution-values)))
        values-to-substitute {:title "Submitted"
                              :message "Your events mail has been submitted."}]
    (apply str (template values-to-substitute))))
  
(defn events [servlet request response]
  (let [event-title (.getParameter request "name")
        message (mime-message-from request)]
    (save-incoming-message message)
    (show-user-confirmation servlet)))
