(ns com.cyrusinnovation.mail-scraper.handlers.events-handler
  (:require [com.cyrusinnovation.mail-scraper.parsers.mime-message-parser :as message-parser])
  (:require [com.cyrusinnovation.mail-scraper.persistence.message-persistence :as persister])
	(:require [com.cyrusinnovation.mail-scraper.utils.template-utils :as utils])
  (:require [net.cgrand.enlive-html :as html]))

(defn save-incoming-message [input-stream]
  (let [message-record (message-parser/parse input-stream)]
    (persister/store message-record)
    message-record))

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
        input-stream (.getInputStream request)]
    (save-incoming-message input-stream)
    (show-user-confirmation servlet)))
