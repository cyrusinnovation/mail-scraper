(ns com.cyrusinnovation.mail-scraper.handlers.events-handler
  (:require [appengine-clj.datastore :as ds])
	(:require [com.cyrusinnovation.mail-scraper.utils.template-utils :as utils])
  (:require [net.cgrand.enlive-html :as html]))

(defn events [servlet request response]
  (let [event-title (.getParameter request "name")
        
        ;; mime-message (msg-from-request request)
        ;; mail-message-string (mail-message-body mime-message)
        ;; mail-message-text (new Text mail-message-string)
        ;; message-from (from-address )
        
        template (utils/prepare-template servlet "events"
																	 [substitution-values]
                                   [:title] (html/content (:title substitution-values))
																	 [:h2.message] (html/content (:message substitution-values)))
				values-to-substitute {:title "Submitted"
                              :message "Your events mail has been submitted."}]
    
    (ds/create {:kind "Event"
                ;; :from message-from :date message-date :mail-message-text mail-message-text
                ;; :mail-message-html mail-message-html
                :title event-title })
    (apply str (template values-to-substitute))))
