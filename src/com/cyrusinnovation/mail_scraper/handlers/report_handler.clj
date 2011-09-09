(ns com.cyrusinnovation.mail-scraper.handlers.report-handler
	(:require [com.cyrusinnovation.mail-scraper.utils.template-utils :as utils])
  (:require [appengine-clj.datastore :as ds])
  (:require [net.cgrand.enlive-html :as html])
  (:import (com.google.appengine.api.datastore Query)))
  
(defn fetch-events []
  (ds/find-all (Query. "Event")))

(defn message []
  (let [events (fetch-events)]
    (if (empty? events)
      "There are no networking events at this time."
      ((last events) :title))))

(defn report [servlet request response]
	(let [template (utils/prepare-template servlet "report"
																	 [substitution-values]
                                   [:title] (html/content (:title substitution-values))
																	 [:h2.eventTitle] (html/content (:message substitution-values)))
				values-to-substitute {:title "Networking Events"
                              :message (message)}]
		(apply str (template values-to-substitute))))

  