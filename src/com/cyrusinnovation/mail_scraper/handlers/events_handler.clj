(ns com.cyrusinnovation.mail-scraper.handlers.events-handler
  (:require [appengine-clj.datastore :as ds])
	(:require [com.cyrusinnovation.mail-scraper.utils.template-utils :as utils])
  (:require [net.cgrand.enlive-html :as html]))

(defn events [servlet request response]
  (let [event-title (.getParameter request "name")
        template (utils/prepare-template servlet "events"
																	 [substitution-values]
                                   [:title] (html/content (:title substitution-values))
																	 [:h2.message] (html/content (:message substitution-values)))
				values-to-substitute {:title "Submitted"
                              :message "Your events mail has been submitted."}]
    (ds/create {:kind "Event" :title event-title})
    (apply str (template values-to-substitute))))
